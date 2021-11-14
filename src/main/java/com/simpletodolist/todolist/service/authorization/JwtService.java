package com.simpletodolist.todolist.service.authorization;

import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.domain.entity.redis.JwtStatus;
import com.simpletodolist.todolist.domain.entity.redis.UserJwt;
import com.simpletodolist.todolist.repository.redis.JwtStatusRepository;
import com.simpletodolist.todolist.repository.redis.UserJwtRepository;
import com.simpletodolist.todolist.security.JwtTokenUtil;
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

    public JWT issueNewJwt(String username) {
        return jwtTokenUtil.generateJWT(username);
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
