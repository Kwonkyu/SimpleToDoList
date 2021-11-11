package com.simpletodolist.todolist.service.authorization;

import com.simpletodolist.todolist.controller.bind.JwtRequest;
import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.repository.redis.JwtStatusRepository;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtStatusRepository jwtStatusRepository;

    public JWT refreshAccessToken(JwtRequest request) {
        Claims claims = jwtTokenUtil.parseBearerJWTSubject(request.getRefreshToken()); // expiration check done in util.\
        return jwtTokenUtil.generateJWT(jwtTokenUtil.getUsername(claims));
    }
}
