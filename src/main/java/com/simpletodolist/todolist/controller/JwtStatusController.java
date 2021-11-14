package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.controller.bind.JwtRequest;
import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.security.JwtTokenUtil;
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
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JWT>> refreshAccessToken(@Validated(JwtRequest.Both.class)
                                                               @RequestBody JwtRequest request) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(request.getRefreshToken()));
        jwtService.invalidateExistingUserJwt(username, "refreshing token");
        JWT newJwt = jwtService.issueNewJwt(username);
        jwtService.registerUserJwt(username, newJwt.getAccessToken(), newJwt.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(newJwt));
    }
}
