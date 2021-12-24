package com.simpletodolist.todolist.domains.team.service;

import com.simpletodolist.todolist.domains.team.bind.request.TeamInformationRequest;
import com.simpletodolist.todolist.domains.team.bind.request.TeamSearchRequest;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.team.entity.Team;
import com.simpletodolist.todolist.domains.member.repository.MemberRepository;
import com.simpletodolist.todolist.domains.team.repository.TeamRepository;
import com.simpletodolist.todolist.domains.todolist.repository.TodoListRepository;
import com.simpletodolist.todolist.common.util.EntityFinder;
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
    private final EntityFinder entityFinder;


    @Transactional(readOnly = true)
    public TeamDTO readTeam(long teamId) {
        return new TeamDTO(entityFinder.findTeamById(teamId));
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> searchTeams(TeamSearchRequest request, String username) {
        List<Team> result;
        switch (request.getSearchTeamField()) {
            case LEADER:
                Member leader = entityFinder.findMemberByUsername(request.getSearchValue());
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
            return stream
                    .map(TeamDTO::new)
                    .collect(Collectors.toList());
        } else {
            Member member = entityFinder.findMemberByUsername(username);
            return stream
                    .filter(team -> !team.getMembersReadOnly().contains(member))
                    .map(TeamDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public List<MemberDTO> getMembers(long teamId) {
        return entityFinder.findTeamById(teamId).getMembersReadOnly().stream()
                .map(MemberDTO::new)
                .collect(Collectors.toList());
    }

    public TeamDTO createTeam(String username, TeamInformationRequest request) {
        Member member = entityFinder.findMemberByUsername(username);
        Team team = new Team(member, request.getTeamName(), false);
        team.addMember(member);
        return new TeamDTO(teamRepository.save(team));
    }

    public TeamDTO updateTeam(long teamId, TeamInformationRequest request) {
        Team team = entityFinder.findTeamById(teamId);
        team.changeTeamName(request.getTeamName());
        if (request.isLocked()) {
            team.lock();
        } else {
            team.unlock();
        }
        return new TeamDTO(team);
    }

    public void deleteTeam(long teamId) {
        Team team = entityFinder.findTeamById(teamId);
        team.getMembersReadOnly().forEach(memberRepository::delete);
        team.getTodoLists().forEach(todoListRepository::delete);
        teamRepository.delete(team);
    }

    public List<MemberDTO> joinMember(long teamId, String username) {
        Team team = entityFinder.findTeamById(teamId);
        Member member = entityFinder.findMemberByUsername(username);
        team.addMember(member);
        return team.getMembersReadOnly().stream()
                .map(MemberDTO::new)
                .collect(Collectors.toList());
    }

    public List<MemberDTO> withdrawMember(long teamId, String username) {
        Team team = entityFinder.findTeamById(teamId);
        Member member = entityFinder.findMemberByUsername(username);
        team.removeMember(member); // 당연한 거 아냐? 세션에 남아있는걸.
        return team.getMembersReadOnly().stream().map(MemberDTO::new).collect(Collectors.toList());
    }

    public TeamDTO changeLeader(long teamId, String username) {
        Team team = entityFinder.findTeamById(teamId);
        Member member = entityFinder.findMemberByUsername(username);
        team.changeLeader(member);
        return new TeamDTO(team);
    }
}
