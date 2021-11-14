package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.controller.bind.member.MemberInformationRequest;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.JwtService;
import com.simpletodolist.todolist.service.member.BasicMemberService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemberTestMaster {
    private final BasicMemberService memberService;
    private final JwtService jwtService;

    private final static Map<String, MemberDTO> members = new HashMap<>();

    public MemberTestMaster(BasicMemberService memberService, JwtService jwtService) {
        this.memberService = memberService;
        this.jwtService = jwtService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, Math.min(length, 32));
    }

    public MemberDTO createNewMember() {
        return createNewMember(randomString(31));
    }
    public MemberDTO createNewMember(String username) {
        String originalPassword = randomString(32);
        MemberInformationRequest newMember = new MemberInformationRequest();
        newMember.setUsername(username);
        newMember.setAlias(randomString(16));
        newMember.setPassword(originalPassword);

        MemberDTO response = memberService.registerMember(newMember);
        response.setPassword(originalPassword);
        members.put(username, response);
        return response;
    }

    public MemberDTO getMemberInfo(String username) {
        return members.get(username);
    }

    public String getRequestToken(String username) {
        return String.format("Bearer %s", jwtService.issueNewJwt(username).getAccessToken());
    }
}
