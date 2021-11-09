package com.simpletodolist.todolist.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.controller.bind.member.MemberLoginRequest;
import com.simpletodolist.todolist.controller.bind.member.MemberInformationRequest;
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

    /**
     * Login member account with username and password.
     * @param request Object containing username and password.
     * @return Logged-in member's DTO.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberDTO>> login(@RequestBody @Valid MemberLoginRequest request) throws JsonProcessingException {
        MemberDTO memberDTO = memberService.authenticateMember(request.getUsername(), request.getPassword());
        String token = jwtTokenUtil.generateAccessToken(request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(memberDTO, token));
    }

    /**
     * Register new user.
     * @param request Registering user's information.
     * @return Created member's DTO.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<MemberDTO>> registerMember(@Valid @RequestBody MemberInformationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.registerMember(request)));
    }
}
