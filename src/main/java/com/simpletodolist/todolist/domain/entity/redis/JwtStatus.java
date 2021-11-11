package com.simpletodolist.todolist.domain.entity.redis;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@RedisHash(value = "jwtStatus", timeToLive = 60 * 60 * 24)
@Getter
@NoArgsConstructor
public class JwtStatus {
    @Id String id;
    String username;
    String accessToken;
    String refreshToken;

    @Builder
    public JwtStatus(String username, String accessToken, String refreshToken) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
