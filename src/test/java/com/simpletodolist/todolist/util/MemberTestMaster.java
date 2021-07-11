package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.controller.bind.MemberDTO;
import com.simpletodolist.todolist.service.member.MemberService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemberTestMaster {

    private final static Map<String, MemberDTO> members = new HashMap<>();

    private final MemberService memberService;

    public MemberTestMaster(MemberService memberService) {
        this.memberService = memberService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, Math.min(length, 32));
    }

    public MemberDTO createNewMember() {
        return createNewMember(randomString(31));
    }
    public MemberDTO createNewMember(String userId) {
        String originalPassword = randomString(32);
        MemberDTO newMember = new MemberDTO(userId, randomString(16), originalPassword);
        newMember.setId(memberService.registerMember(newMember).getId());
        newMember.setPassword(originalPassword);
        members.put(userId, newMember);
        return newMember;
    }

    public MemberDTO getMemberInfo(String userId) {
        return members.get(userId);
    }

    public String getRequestToken(String userId, String password) {
        return String.format("Bearer %s", memberService.loginMember(userId, password).getToken());
    }
}
