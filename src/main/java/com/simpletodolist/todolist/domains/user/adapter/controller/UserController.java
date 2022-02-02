package com.simpletodolist.todolist.domains.user.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserUpdateRequest;
import com.simpletodolist.todolist.domains.user.domain.User;
import com.simpletodolist.todolist.domains.user.service.port.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserCrudService userCrudService;

    @GetMapping
    public ResponseEntity<ApiResponse<User>> memberInfo(
        @AuthenticationPrincipal UserDetails userDetails
        ){
        return ResponseEntity.ok(ApiResponse.success(
                userCrudService.getUserDetails(userDetails.getUsername())));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<User>> updateMemberInfo(
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
            userCrudService.updateUser(userDetails.getUsername(), request)));
    }
    // TODO: UserEntity 대신 User에 UserDetails를 등록해서 이후 세션에서 꺼내쓰도록?

    @DeleteMapping
    public ResponseEntity<Object> withdrawMember(
        @AuthenticationPrincipal UserDetails userDetails){
        userCrudService.withdrawUser(userDetails.getUsername());
        return ResponseEntity.noContent()
                             .build();
    }
}
