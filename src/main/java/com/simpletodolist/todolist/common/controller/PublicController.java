package com.simpletodolist.todolist.common.controller;

import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.member.bind.request.MemberLoginRequest;
import com.simpletodolist.todolist.domains.member.bind.request.MemberInformationRequest;
import com.simpletodolist.todolist.domains.jwt.JwtResponse;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.jwt.service.JwtService;
import com.simpletodolist.todolist.domains.member.service.BasicMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {
    private final BasicMemberService memberService;
    private final JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody @Valid MemberLoginRequest request) {
        memberService.authenticateMember(request.getUsername(), request.getPassword()); // replace auth work with annotation?
        JwtResponse token = jwtService.issueNewJwt(request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(token, "Expiration date of access token: 1 day, refresh token: 1 week"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<MemberDTO>> registerMember(@Valid @RequestBody MemberInformationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.registerMember(request)));
    }
}
