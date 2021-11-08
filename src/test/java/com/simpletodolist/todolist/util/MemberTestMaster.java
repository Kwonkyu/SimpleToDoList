package com.simpletodolist.todolist.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simpletodolist.todolist.controller.bind.member.MemberInformationRequest;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.member.BasicMemberService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemberTestMaster {
    private final BasicMemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    private final static Map<String, MemberDTO> members = new HashMap<>();

    public MemberTestMaster(BasicMemberService memberService, JwtTokenUtil jwtTokenUtil) {
        this.memberService = memberService;
        this.jwtTokenUtil = jwtTokenUtil;
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

    public String getRequestToken(String userId, String password) throws JsonProcessingException {
        return String.format("Bearer %s", jwtTokenUtil.generateAccessToken(userId));
    }
}
