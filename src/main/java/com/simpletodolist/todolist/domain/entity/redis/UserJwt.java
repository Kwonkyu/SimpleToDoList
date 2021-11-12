package com.simpletodolist.todolist.domain.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@RedisHash(value = "userJwt", timeToLive = 60 * 60 * 24 * 7)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserJwt {
    @Id String username;
    String accessToken;
    String refreshToken;
}
