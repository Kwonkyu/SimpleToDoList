package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.controller.bind.JwtRequest;
import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.service.authorization.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class JwtStatusController {
    private final JwtService jwtService;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JWT>> refreshAccessToken(@Validated(JwtRequest.Refresh.class) @RequestBody
                                                              JwtRequest request) {
        JWT jwt = jwtService.refreshAccessToken(request);
        return ResponseEntity.ok(ApiResponse.success(jwt));
    }
}
