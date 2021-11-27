package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.controller.bind.member.MemberLoginRequest;
import com.simpletodolist.todolist.controller.bind.member.MemberInformationRequest;
import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.service.authorization.JwtService;
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
    private final JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JWT>> login(@RequestBody @Valid MemberLoginRequest request) {
        memberService.authenticateMember(request.getUsername(), request.getPassword()); // replace auth work with annotation?
        JWT token = jwtService.issueNewJwt(request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(token, "Expiration date of access token: 1 day, refresh token: 1 week"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<MemberDTO>> registerMember(@Valid @RequestBody MemberInformationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.registerMember(request)));
    }
}
