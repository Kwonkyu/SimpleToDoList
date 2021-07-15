package com.simpletodolist.todolist.service.team;

import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.domain.bind.TodoListDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.member.DuplicatedTeamJoinException;
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
    public List<TeamDTO.BasicWithJoined> searchTeams(SearchRequest.SearchTeamField field, Object value, String searcherUserId, boolean includeJoined) {
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

        return result.stream()
                .map(team -> new TeamDTO.BasicWithJoined(
                team, memberTeamAssocRepository.existsByTeamAndMember(team, member)))
                .collect(Collectors.toList());

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
    public Basic updateTeam(long teamId, UpdateRequest.UpdatableTeamInformation field, Object value) throws NoTeamFoundException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        String changedValue = String.valueOf(value);
        switch (field) {
            case NAME:
                team.changeTeamName(changedValue.length() > 64 ? changedValue.substring(0, 64) : changedValue);
                break;

            case LOCKED:
                boolean request = Boolean.parseBoolean(changedValue);
                if(request) team.lock();
                else team.unlock();
                break;
        }
        return new TeamDTO.Basic(team);
    }

    @Override
    public void deleteTeam(long teamId) throws NoTeamFoundException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        team.getMembers().forEach(member -> memberTeamAssocRepository.deleteByTeamAndMember(team, member));
        teamRepository.delete(team);
    }

    @Override
    @Transactional(readOnly = true)
    public Response getTeamDetails(long teamId) {
        return new Response(teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Basic getTeamInformation(long teamId) throws NoTeamFoundException {
        return new Basic(teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDTO.BasicWithTodoLists> getTeamMembers(long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        List<Member> members = team.getMembers();
        return members.stream()
                .map(member -> {
                    List<TodoList> todoLists = todoListRepository.findAllByOwnerAndTeam(member, team);
                    return new MemberDTO.BasicWithTodoLists(member, todoLists);
                })
                .collect(Collectors.toList());
    }

    @Override
    public MemberDTO.BasicWithTodoLists getTeamMemberInformation(long teamId, String userId) throws NoTeamFoundException, NoMemberFoundException, NotJoinedMemberException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(userId).orElseThrow(NoMemberFoundException::new);
        if (!memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new NotJoinedMemberException();
        return new MemberDTO.BasicWithTodoLists(member, todoListRepository.findAllByOwnerAndTeam(member, team));
    }

    @Override
    public List<TodoListDTO.Response> getTeamTodoLists(long teamId) throws NoTeamFoundException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        return todoListRepository.findAllByTeam(team).stream().map(TodoListDTO.Response::new).collect(Collectors.toList());
    }

    @Override
    public List<MemberDTO.Basic> joinMember(long teamId, String memberUserId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        if(memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new DuplicatedTeamJoinException();

        memberTeamAssocRepository.save(new MemberTeamAssociation(member, team));
        return team.getMembers().stream().map(MemberDTO.Basic::new).collect(Collectors.toList());
    }

    @Override
    public List<MemberDTO.Basic> withdrawMember(long teamId, String memberUserId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        if(!memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new NotJoinedMemberException();
        memberTeamAssocRepository.deleteByTeamAndMember(team, member);
        memberTeamAssocRepository.flush(); // TODO: note here. Why it doesn't work without flush? https://github.com/spring-projects/spring-data-jpa/issues/1100
        return team.getMembers().stream().map(MemberDTO.Basic::new).collect(Collectors.toList());
    }

    @Override
    public Basic changeLeader(long teamId, String memberUserId) throws NoTeamFoundException, NoMemberFoundException, NotJoinedMemberException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        // New leader should be one of member.
        if(!memberTeamAssocRepository.existsByTeamAndMember(team, member)) {
            throw new NotJoinedMemberException(
                    NotJoinedMemberException.DEFAULT_ERROR,
                    messageSource.getMessage("unauthorized.leader.not.joined", null, Locale.KOREAN));
        }
        team.changeLeader(member);
        return new Basic(team);
    }
}
