package com.simpletodolist.todolist.domains.user.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserUpdateRequest;
import com.simpletodolist.todolist.domains.user.domain.User;
import com.simpletodolist.todolist.domains.user.service.port.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserCrudService userCrudService;

    @GetMapping
    public ResponseEntity<ApiResponse<User>> memberInfo(
        @AuthenticationPrincipal Authentication authentication
        ){
        return ResponseEntity.ok(ApiResponse.success(
            userCrudService.getMemberDetails(authentication.getName())));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<User>> updateMemberInfo(
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
            userCrudService.updateMember(authentication.getName(), request)));
    }

    @DeleteMapping
    public ResponseEntity<Object> withdrawMember(
        @AuthenticationPrincipal Authentication authentication){
        userCrudService.withdrawMember(authentication.getName());
        return ResponseEntity.noContent()
                             .build();
    }
}
