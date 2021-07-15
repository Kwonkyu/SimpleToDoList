package com.simpletodolist.todolist.service.member;

import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.exception.member.DuplicatedMemberException;
import com.simpletodolist.todolist.exception.member.DuplicatedTeamJoinException;
import com.simpletodolist.todolist.exception.member.LockedMemberException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.LockedTeamException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.team.NotJoinedTeamException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.MemberTeamAssocRepository;
import com.simpletodolist.todolist.repository.TeamRepository;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.simpletodolist.todolist.domain.bind.MemberDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMemberService implements MemberService{

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final MemberTeamAssocRepository memberTeamAssocRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional(readOnly = true)
    public Response getMemberDetails(String memberUserId) {
        return new Response(memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Basic getMemberInformation(String memberUserId) throws NoMemberFoundException {
        return new Basic(memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new));
    }

    @Override
    public LoginResponse loginMember(String memberUserId, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberUserId, rawPassword));

        Member member = (Member) authentication.getPrincipal();
        if(member.isLocked()) throw new LockedMemberException();
        return new LoginResponse(member, jwtTokenUtil.generateAccessToken(member.getUserId()));
    }

    @Override
    public Response registerMember(RegisterRequest registerRequest) {
        if(memberRepository.existsByUserId(registerRequest.getUserId())) throw new DuplicatedMemberException();
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Member save = memberRepository.save(new Member(registerRequest.getUserId(), registerRequest.getUsername(), registerRequest.getPassword()));
        return new Response(save);
    }

    @Override
    public Basic updateMember(String memberUserId, UpdateRequest.UpdatableMemberInformation update, Object value) throws NoMemberFoundException {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        String changedValue = String.valueOf(value);
        switch(update) {
            case USERNAME:
                if(changedValue.length() > 32) changedValue = changedValue.substring(0, 32);
                member.changeUsername(changedValue);
                break;

            case PASSWORD:
                if(changedValue.length() > 64) changedValue = changedValue.substring(0, 64);
                member.changePassword(passwordEncoder.encode(changedValue));
                break;
        }
        return new Basic(member);
    }

    @Override
    public void withdrawMember(String memberUserId) throws NoMemberFoundException {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        member.getTeams().forEach(memberTeamAssocRepository::delete);
        memberRepository.delete(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDTO.Basic> getTeamsOfMember(String memberUserId) {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        return member.getTeams().stream()
                .map(MemberTeamAssociation::getTeam)
                .map(TeamDTO.Basic::new)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUserId(username).orElseThrow(NoMemberFoundException::new);
    }

    @Override
    public List<MemberDTO.Basic> joinTeam(long teamId, String userId) throws NoTeamFoundException, LockedTeamException, DuplicatedTeamJoinException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(userId).orElseThrow(NoMemberFoundException::new);

        if(memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new DuplicatedTeamJoinException();
        if(team.isLocked()) throw new LockedTeamException();

        memberTeamAssocRepository.save(new MemberTeamAssociation(member, team));
        return team.getMembers().stream().map(MemberDTO.Basic::new).collect(Collectors.toList());
    }

    @Override
    public void withdrawTeam(long teamId, String userId) throws NoTeamFoundException, NotJoinedTeamException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(userId).orElseThrow(NoMemberFoundException::new);

        if(!memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new NotJoinedTeamException();
        else memberTeamAssocRepository.deleteByTeamAndMember(team, member);
    }
}
