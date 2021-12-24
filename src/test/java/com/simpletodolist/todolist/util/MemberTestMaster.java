package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.domains.member.bind.request.MemberInformationRequest;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.jwt.service.JwtService;
import com.simpletodolist.todolist.domains.member.service.BasicMemberService;

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
