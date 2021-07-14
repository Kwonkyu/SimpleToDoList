package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

import static com.simpletodolist.todolist.domain.bind.MemberDTO.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {

    private final MemberService memberService;


    /**
     * Login member account with user id and password.
     * @param loginRequest MemberDTO.LoginRequest object containing user id and password.
     * @return MemberDTO object filled with logged in member's information.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(memberService.loginMember(loginRequest.getUserId(), loginRequest.getPassword()));
    }


    /**
     * Register new user.
     * @param registerRequest Registering user's information.
     * @return 200 OK with body filled with registered user info.
     */
    @PostMapping("/register")
    public ResponseEntity<Response> registerMember(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(memberService.registerMember(registerRequest));
    }
}
