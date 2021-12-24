package com.simpletodolist.todolist.domains.member.service;

import com.simpletodolist.todolist.domains.member.bind.request.MemberInformationRequest;
import com.simpletodolist.todolist.domains.member.bind.request.MemberUpdateRequest;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.team.entity.MemberTeamAssociation;
import com.simpletodolist.todolist.domains.team.entity.Team;
import com.simpletodolist.todolist.domains.member.exception.DuplicatedMemberException;
import com.simpletodolist.todolist.domains.member.exception.NoMemberFoundException;
import com.simpletodolist.todolist.domains.team.exception.TeamAccessException;
import com.simpletodolist.todolist.domains.member.repository.MemberRepository;
import com.simpletodolist.todolist.common.util.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMemberService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final EntityFinder entityFinder;


    @Transactional(readOnly = true)
    public MemberDTO getMemberDetails(String username) {
        return new MemberDTO(entityFinder.findMemberByUsername(username));
    }

    @Transactional(readOnly = true)
    public void authenticateMember(String username, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, rawPassword));

        Member member = (Member) authentication.getPrincipal();
        if(member == null) throw new NoMemberFoundException(username);
    }

    public MemberDTO registerMember(MemberInformationRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new DuplicatedMemberException(request.getUsername());
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        Member member = Member.builder()
                .username(request.getUsername())
                .alias(request.getAlias())
                .password(encryptedPassword)
                .locked(false).build();

        memberRepository.save(member);
        return new MemberDTO(member);
    }

    public MemberDTO updateMember(String username, MemberUpdateRequest request) {
        Member member = entityFinder.findMemberByUsername(username);
        member.changeAlias(request.getAlias());
        member.changePassword(passwordEncoder.encode(request.getPassword()));
        return new MemberDTO(member);
    }

    public void withdrawMember(String username) {
        Member member = entityFinder.findMemberByUsername(username);
        List<Team> joinedTeams = member.getTeams().stream()
                .map(MemberTeamAssociation::getTeam)
                .collect(Collectors.toList());
        joinedTeams.forEach(team -> team.removeMember(member));
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> getJoinedTeams(String username) {
        Member member = entityFinder.findMemberByUsername(username);
        return member.getTeamsReadOnly().stream()
                .map(TeamDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return entityFinder.findMemberByUsername(username);
    }

    public TeamDTO joinTeam(long teamId, String username) {
        Team team = entityFinder.findTeamById(teamId);
        Member member = entityFinder.findMemberByUsername(username);

        if (team.isLocked()) {
            throw new TeamAccessException(team);
        }

        team.addMember(member);
        return new TeamDTO(team);
    }

    public void withdrawTeam(long teamId, String username) {
        Team team = entityFinder.findTeamById(teamId);
        Member member = entityFinder.findMemberByUsername(username);
        team.removeMember(member);
    }
}
