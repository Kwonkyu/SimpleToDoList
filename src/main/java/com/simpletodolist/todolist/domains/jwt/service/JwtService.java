package com.simpletodolist.todolist.domains.jwt.service;

import com.simpletodolist.todolist.domains.jwt.JwtResponse;
import com.simpletodolist.todolist.domains.jwt.JwtStatus;
import com.simpletodolist.todolist.domains.jwt.UserJwt;
import com.simpletodolist.todolist.domains.jwt.repository.JwtStatusRepository;
import com.simpletodolist.todolist.domains.jwt.repository.UserJwtRepository;
import com.simpletodolist.todolist.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtStatusRepository jwtStatusRepository;
    private final UserJwtRepository userJwtRepository;


    public void invalidateExistingUserJwt(String username) {
        invalidateExistingUserJwt(username, "invalidation request");
    }

    public void invalidateExistingUserJwt(String username, String reason) {
        userJwtRepository.findById(username).ifPresent(userJwt ->
                invalidateUserJwt(userJwt.getAccessToken(), userJwt.getRefreshToken(), reason));
    }

    public void invalidateUserJwt(String accessToken, String refreshToken) {
        invalidateUserJwt(accessToken, refreshToken, "invalidation request");
    }

    public void invalidateUserJwt(String accessToken, String refreshToken, String reason) {
        jwtStatusRepository.save(JwtStatus.invalidated(accessToken, reason, JwtStatus.ACCESS_TOKEN_TTL));
        jwtStatusRepository.save(JwtStatus.invalidated(refreshToken,reason, JwtStatus.REFRESH_TOKEN_TTL));
    }

    public JwtResponse issueNewJwt(String username) {
        invalidateExistingUserJwt(username);
        JwtResponse jwtResponse = jwtTokenUtil.generateJWT(username);
        registerUserJwt(username, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
        return jwtResponse;
    }

    public void registerUserJwt(String username, String accessToken, String refreshToken) {
        userJwtRepository.findById(username).ifPresentOrElse(
                userJwt -> {
                    userJwt.setAccessToken(accessToken);
                    userJwt.setRefreshToken(refreshToken);
                    userJwtRepository.save(userJwt);
                },
                () -> userJwtRepository.save(new UserJwt(username, accessToken, refreshToken)));
    }
}
