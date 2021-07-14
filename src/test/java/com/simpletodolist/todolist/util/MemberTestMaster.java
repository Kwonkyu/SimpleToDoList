package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.service.member.MemberService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemberTestMaster {

    private final static Map<String, MemberDTO.Response> members = new HashMap<>();

    private final MemberService memberService;

    public MemberTestMaster(MemberService memberService) {
        this.memberService = memberService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, Math.min(length, 32));
    }

    public MemberDTO.Response createNewMember() {
        return createNewMember(randomString(31));
    }
    public MemberDTO.Response createNewMember(String userId) {
        String originalPassword = randomString(32);
        MemberDTO.RegisterRequest newMember = MemberDTO.RegisterRequest.builder()
                .userId(userId).username(randomString(16)).password(originalPassword).build();
        MemberDTO.Response response = memberService.registerMember(newMember);
        response.setPassword(originalPassword);
        members.put(userId, response);
        return response;
    }

    public MemberDTO.Response getMemberInfo(String userId) {
        return members.get(userId);
    }

    public String getRequestToken(String userId, String password) {
        return String.format("Bearer %s", memberService.loginMember(userId, password).getToken());
    }
}
