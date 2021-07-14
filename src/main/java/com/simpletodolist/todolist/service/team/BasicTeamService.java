package com.simpletodolist.todolist.service.team;

import com.simpletodolist.todolist.domain.bind.*;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.exception.member.DuplicatedTeamJoinException;
import com.simpletodolist.todolist.exception.member.InvalidTeamWithdrawException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.member.NotJoinedMemberException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.MemberTeamAssocRepository;
import com.simpletodolist.todolist.repository.TeamRepository;
import com.simpletodolist.todolist.repository.TodoListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.simpletodolist.todolist.domain.bind.TeamDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicTeamService implements TeamService{

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TodoListRepository todoListRepository;
    private final MemberTeamAssocRepository memberTeamAssocRepository;
    private final MessageSource messageSource;


    @Override
    public boolean isTeamLocked(long teamId) throws NoTeamFoundException {
        return teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new).isLocked();
    }

    @Override
    public List<TeamDTO.Response> searchTeams(SearchRequest.SearchTeamField field, Object value, String searcherUserId, boolean includeJoined) {
        List<Team> result;
        Member member = memberRepository.findByUserId(String.valueOf(searcherUserId)).orElseThrow(NoMemberFoundException::new);

        switch (field) {
            case LEADER:
                String leaderUserId = String.valueOf(value);
                Member leader = memberRepository.findByUserId(leaderUserId).orElseThrow(NoMemberFoundException::new);
                result = includeJoined ?
                        teamRepository.findAllByLeader(leader) :
                        teamRepository.findAllByLeaderAndNotJoined(leader, member);
                break;

            case NAME:
                String teamName = String.valueOf(value);
                result = includeJoined ?
                        teamRepository.findAllByTeamNameContaining(teamName) :
                        teamRepository.findAllByTeamNameLikeAndNotJoined(teamName, member);
                break;

            default:
                result = new ArrayList<>();
        }

        return result.stream().map(TeamDTO.Response::new).collect(Collectors.toList());
    }

    @Override
    public Response createTeam(String memberUserId, RegisterRequest teamDTO) throws NoMemberFoundException {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        Team team = new Team(member, teamDTO.getTeamName());
        teamRepository.save(team);
        memberTeamAssocRepository.save(new MemberTeamAssociation(member, team));
        return new TeamDTO.Response(team);
    }

    @Override
    public Response updateTeam(long teamId, UpdateRequest.UpdatableTeamInformation field, Object value) throws NoTeamFoundException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        switch (field) {
            case NAME:
                team.changeTeamName((String) value);
                break;

            case LOCKED:
                boolean request = (boolean) value;
                if(request) team.lock();
                else team.unlock();
                break;
        }
        return new TeamDTO.Response(team);
    }

    @Override
    public void deleteTeam(long teamId) throws NoTeamFoundException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        team.getMembers().forEach(memberTeamAssocRepository::delete);
        teamRepository.delete(team);
    }

    @Override
    @Transactional(readOnly = true)
    public Response getTeamDetails(long teamId) {
        return new TeamDTO.Response(teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDTO.Response> getTeamMembers(long teamId) {
        return teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new).getMembersDTO();
    }

    @Override
    public List<TodoListDTO.Response> getTeamTodoLists(long teamId) throws NoTeamFoundException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        return todoListRepository.findAllByTeam(team).stream().map(TodoListDTO.Response::new).collect(Collectors.toList());
    }

    @Override
    public List<MemberDTO.Response> joinMember(long teamId, String memberUserId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        if(memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new DuplicatedTeamJoinException();

        memberTeamAssocRepository.save(new MemberTeamAssociation(member, team));
        return team.getMembersDTO();
    }

    @Override
    public List<MemberDTO.Response> withdrawMember(long teamId, String memberUserId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        if(!memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new InvalidTeamWithdrawException();
        memberTeamAssocRepository.deleteByTeamAndMember(team, member);
        memberTeamAssocRepository.flush(); // TODO: note here. Why it doesn't work without flush? https://github.com/spring-projects/spring-data-jpa/issues/1100
        return team.getMembersDTO();
    }

    @Override
    public Response changeLeader(long teamId, String memberUserId) throws NoTeamFoundException, NoMemberFoundException, NotJoinedMemberException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        // New leader should be one of member.
        if(!memberTeamAssocRepository.existsByTeamAndMember(team, member)) {
            throw new NotJoinedMemberException(
                    NotJoinedMemberException.DEFAULT_ERROR,
                    messageSource.getMessage("unauthorized.leader.not.joined", null, Locale.KOREAN));
        }
        team.changeLeader(member);
        return new Response(team);
    }
}
