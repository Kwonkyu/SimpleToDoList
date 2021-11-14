package com.simpletodolist.todolist.domain.entity.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

@RedisHash(value = "jwtStatus")
@Getter
@Setter
@NoArgsConstructor
public class JwtStatus {
    public static final long ACCESS_TOKEN_TTL = 60 * 60 * 24L;
    public static final long REFRESH_TOKEN_TTL = ACCESS_TOKEN_TTL * 7L;

    @Id
    private String token;
    private boolean invalid;
    private LocalDateTime invalidatedDate;
    private String invalidatedReason;
    @TimeToLive
    private Long timeToLive;


    public static JwtStatus invalidated(String token, String reason, long ttl) {
        JwtStatus jwtStatus = new JwtStatus();
        jwtStatus.setToken(token);
        jwtStatus.setInvalid(true);
        jwtStatus.setInvalidatedDate(LocalDateTime.now());
        jwtStatus.setInvalidatedReason(reason);
        jwtStatus.setTimeToLive(ttl);
        return jwtStatus;
    }
}
