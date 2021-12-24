package com.simpletodolist.todolist.domains.jwt.controller;

import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.jwt.bind.JwtRequest;
import com.simpletodolist.todolist.domains.jwt.JwtResponse;
import com.simpletodolist.todolist.common.util.JwtTokenUtil;
import com.simpletodolist.todolist.domains.jwt.service.JwtService;
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
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refreshAccessToken(@Validated(JwtRequest.Both.class)
                                                               @RequestBody JwtRequest request) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(request.getRefreshToken()));
        JwtResponse newJwtResponse = jwtService.issueNewJwt(username);
        return ResponseEntity.ok(ApiResponse.success(newJwtResponse));
    }
}
