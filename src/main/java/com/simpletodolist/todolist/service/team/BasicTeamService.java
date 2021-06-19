package com.simpletodolist.todolist.service.team;

import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.exception.DuplicatedTeamJoinException;
import com.simpletodolist.todolist.exception.InvalidTeamWithdrawException;
import com.simpletodolist.todolist.exception.NoMemberFoundException;
import com.simpletodolist.todolist.exception.NoTeamFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.MemberTeamAssocRepository;
import com.simpletodolist.todolist.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicTeamService implements TeamService{

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final MemberTeamAssocRepository memberTeamAssocRepository;

    @Override
    public TeamDTO createTeam(TeamDTO teamDTO) {
        Member member = memberRepository.findByUserId(teamDTO.getTeamLeaderUserId()).orElseThrow(NoMemberFoundException::new);
        Team team = new Team(member, teamDTO.getTeamName());
        teamRepository.save(team);
        memberTeamAssocRepository.save(new MemberTeamAssociation(member, team));
        return new TeamDTO(team);
    }

    @Override
    public void deleteTeam(long teamId) throws NoTeamFoundException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        team.getMembers().forEach(memberTeamAssocRepository::delete);
        teamRepository.delete(team);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDTO getTeamDetails(long teamId) {
        return new TeamDTO(teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public MembersDTO getTeamMembers(long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        return team.getMembersAsDTO();
    }

    @Override
    public MembersDTO joinMember(long teamId, String memberUserId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        if(member.isJoinedTeam(team)) throw new DuplicatedTeamJoinException();

        memberTeamAssocRepository.save(new MemberTeamAssociation(member, team));
        return team.getMembersAsDTO();
    }

    @Override
    public MembersDTO withdrawMember(long teamId, String memberUserId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        if(!member.isJoinedTeam(team)) throw new InvalidTeamWithdrawException();

        memberTeamAssocRepository.deleteByTeamAndMember(team, member);
        return team.getMembersAsDTO();
    }
}
