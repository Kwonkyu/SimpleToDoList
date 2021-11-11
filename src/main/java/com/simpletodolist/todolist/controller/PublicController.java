package com.simpletodolist.todolist.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.controller.bind.member.MemberLoginRequest;
import com.simpletodolist.todolist.controller.bind.member.MemberInformationRequest;
import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.member.BasicMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {
    private final BasicMemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JWT>> login(@RequestBody @Valid MemberLoginRequest request) throws JsonProcessingException {
        memberService.authenticateMember(request.getUsername(), request.getPassword());
        JWT token = jwtTokenUtil.generateJWT(request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(token, "Expiration date of access token: 1 day, refresh token: 1 week"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<MemberDTO>> registerMember(@Valid @RequestBody MemberInformationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.registerMember(request)));
    }
}
