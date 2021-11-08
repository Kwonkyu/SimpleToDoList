package com.simpletodolist.todolist.service.team;

import com.simpletodolist.todolist.controller.bind.team.TeamInformationRequest;
import com.simpletodolist.todolist.controller.bind.team.TeamSearchRequest;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.MemberTeamAssocRepository;
import com.simpletodolist.todolist.repository.TeamRepository;
import com.simpletodolist.todolist.repository.TodoListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicTeamService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TodoListRepository todoListRepository;
    private final MemberTeamAssocRepository memberTeamAssocRepository;


    private Team findTeamById(long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new NoTeamFoundException(teamId));
    }

    @Transactional(readOnly = true)
    public TeamDTO readTeam(long teamId) {
        return new TeamDTO(findTeamById(teamId));
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> searchTeams(TeamSearchRequest request, String username) {
        List<Team> result;
        switch (request.getSearchTeamField()) {
            case LEADER:
                Member leader = memberRepository.findByUsername(request.getSearchValue()).orElseThrow(() ->
                        new NoMemberFoundException(request.getSearchValue()));
                result = teamRepository.findAllByLeader(leader);
                break;

            case NAME:
                result = teamRepository.findAllByTeamNameContaining(request.getSearchValue());
                break;

            default:
                throw new IllegalArgumentException("Unsupported team search operation.");
        }

        Stream<Team> stream = result.stream();
        if(request.isIncludeJoined()) {
            return stream.map(TeamDTO::new).collect(Collectors.toList());
        } else {
            Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
            return stream.filter(team -> !team.getMembersReadOnly().contains(member)).map(TeamDTO::new).collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public List<MemberDTO> getMembers(long teamId) {
        return findTeamById(teamId).getMembersReadOnly().stream()
                .map(MemberDTO::new)
                .collect(Collectors.toList());
    }

    public TeamDTO createTeam(String username, TeamInformationRequest request) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        Team team = new Team(member, request.getTeamName(), false);
        teamRepository.save(team);
        memberTeamAssocRepository.save(new MemberTeamAssociation(member, team));
        return new TeamDTO(team);
    }

    public TeamDTO updateTeam(long teamId, TeamInformationRequest request) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoTeamFoundException(teamId));
        team.changeTeamName(request.getTeamName());
        if (request.isLocked()) {
            team.lock();
        } else {
            team.unlock();
        }
        return new TeamDTO(team);
    }

    public void deleteTeam(long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoTeamFoundException(teamId));
        team.getMembersReadOnly().forEach(memberRepository::delete);
        team.getTodoLists().forEach(todoListRepository::delete);
        teamRepository.delete(team);
    }

    public List<MemberDTO> joinMember(long teamId, String username) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoTeamFoundException(teamId));
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        team.addMember(member);
        return team.getMembersReadOnly().stream()
                .map(MemberDTO::new)
                .collect(Collectors.toList());
    }

    public List<MemberDTO> withdrawMember(long teamId, String username) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoTeamFoundException(teamId));
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        team.removeMember(member); // 당연한 거 아냐? 세션에 남아있는걸.
        return team.getMembersReadOnly().stream().map(MemberDTO::new).collect(Collectors.toList());
    }

    public TeamDTO changeLeader(long teamId, String username) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoTeamFoundException(teamId));
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        team.changeLeader(member);
        return new TeamDTO(team);
    }
}
