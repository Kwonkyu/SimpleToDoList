package com.simpletodolist.todolist.service.member;

import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.TeamsDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.exception.AuthenticationFailedException;
import com.simpletodolist.todolist.exception.DuplicatedMemberException;
import com.simpletodolist.todolist.exception.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.MemberTeamAssocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMemberService implements MemberService{

    private final MemberRepository memberRepository;
    private final MemberTeamAssocRepository memberTeamAssocRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberDetails(String memberUserId) {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        return new MemberDTO(member);
    }

    @Override
    public MemberDTO registerMember(MemberDTO memberDTO) {
        if(memberRepository.existsByUserId(memberDTO.getUserId())) throw new DuplicatedMemberException();
        memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        Member save = memberRepository.save(new Member(memberDTO));
        return new MemberDTO(save);
    }

    @Override
    public void withdrawMember(String memberUserId, String rawPassword) throws NoMemberFoundException {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        if(!passwordEncoder.matches(rawPassword, member.getPassword())) throw new AuthenticationFailedException();
        member.getTeams().forEach(memberTeamAssocRepository::delete);
        memberRepository.delete(member);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamsDTO getTeamsOfMember(String memberUserId) {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        return member.getTeamsAsDTO();
    }
}
