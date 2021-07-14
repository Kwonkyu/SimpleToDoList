package com.simpletodolist.todolist.service.member;

import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.exception.general.AuthenticationFailedException;
import com.simpletodolist.todolist.exception.member.DuplicatedMemberException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.MemberTeamAssocRepository;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.simpletodolist.todolist.domain.bind.MemberDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMemberService implements MemberService{

    private final MemberRepository memberRepository;
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
    public LoginResponse loginMember(String memberUserId, String rawPassword) throws AuthenticationFailedException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberUserId, rawPassword));

            Member member = (Member) authentication.getPrincipal();
            return new LoginResponse(member, jwtTokenUtil.generateAccessToken(member.getUserId()));
        } catch (BadCredentialsException exception) {
            throw new AuthenticationFailedException();
        }
    }

    @Override
    public Response registerMember(RegisterRequest registerRequest) {
        if(memberRepository.existsByUserId(registerRequest.getUserId())) throw new DuplicatedMemberException();
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Member save = memberRepository.save(new Member(registerRequest.getUserId(), registerRequest.getUsername(), registerRequest.getPassword()));
        return new Response(save);
    }

    @Override
    public Response updateMember(String memberUserId, UpdateRequest.UpdatableMemberInformation update, Object value) throws NoMemberFoundException {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        switch(update) {
            case USERNAME:
                member.changeUsername((String) value);
                break;

            case PASSWORD:
                member.changePassword(passwordEncoder.encode((String) value));
                break;

            case LOCKED:
                boolean command = (boolean) value;
                if(command) member.lock();
                else member.unlock();
        }
        return new Response(member);
    }

    @Override
    public void withdrawMember(String memberUserId) throws NoMemberFoundException {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        member.getTeams().forEach(memberTeamAssocRepository::delete);
        memberRepository.delete(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDTO.Response> getTeamsOfMember(String memberUserId) {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        return member.getTeamsDTO();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUserId(username).orElseThrow(NoMemberFoundException::new);
    }
}
