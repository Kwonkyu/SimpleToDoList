package com.simpletodolist.todolist.service.member;

import com.simpletodolist.todolist.controller.bind.member.MemberInformationRequest;
import com.simpletodolist.todolist.controller.bind.member.MemberUpdateRequest;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.exception.member.DuplicatedMemberException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.team.TeamAccessException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.TeamRepository;
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
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @Transactional(readOnly = true)
    public MemberDTO getMemberDetails(String username) {
        return new MemberDTO(memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username)));
    }

    @Transactional(readOnly = true)
    public MemberDTO authenticateMember(String username, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, rawPassword));

        Member member = (Member) authentication.getPrincipal();
        return new MemberDTO(member);
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
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        member.changeAlias(request.getAlias());
        member.changePassword(passwordEncoder.encode(request.getPassword()));
        return new MemberDTO(member);
    }

    public void withdrawMember(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        List<Team> joinedTeams = member.getTeams().stream().map(MemberTeamAssociation::getTeam).collect(Collectors.toList());
        joinedTeams.forEach(team -> team.removeMember(member));
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> getJoinedTeams(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        return member.getTeamsReadOnly().stream()
                .map(TeamDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
    }

    public TeamDTO joinTeam(long teamId, String username) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoTeamFoundException(teamId));
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));

        if (team.isLocked()) {
            throw new TeamAccessException(team);
        }

        team.addMember(member);
        return new TeamDTO(team);
    }

    public void withdrawTeam(long teamId, String username) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoTeamFoundException(teamId));
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        team.removeMember(member);
    }
}
