package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.LoginDTO;
import com.simpletodolist.todolist.controller.bind.MemberDTO;
import com.simpletodolist.todolist.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {

    private final MemberService memberService;


    /**
     * Login member account with user id and password.
     * @param memberDTO MemberDTO object containing user id and password.
     * @return MemberDTO object filled with logged in member's information.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@RequestBody @Validated(MemberDTO.LoginValidationGroup.class) MemberDTO memberDTO) {
        return ResponseEntity.ok(memberService.loginMember(memberDTO.getUserId(), memberDTO.getPassword()));
    }


    /**
     * Register new user.
     * @param memberDTO Registering user's information.
     * @return 200 OK with body filled with registered user info.
     */
    @PostMapping("/register")
    public ResponseEntity<MemberDTO> registerMember(@Validated(MemberDTO.RegisterValidationGroup.class) @RequestBody MemberDTO memberDTO) {
        return ResponseEntity.ok(memberService.registerMember(memberDTO));
    }
}
