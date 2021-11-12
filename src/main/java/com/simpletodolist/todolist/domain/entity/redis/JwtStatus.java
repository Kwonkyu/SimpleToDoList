package com.simpletodolist.todolist.domain.entity.redis;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.time.LocalDateTime;

@RedisHash(value = "jwtStatus", timeToLive = 60 * 60 * 24 * 7)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtStatus {
    @Id String token;
    boolean invalid;
    LocalDateTime invalidatedDate;
    String invalidatedReason;
}
