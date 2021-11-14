package com.simpletodolist.todolist.domain.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@RedisHash(value = "userJwt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJwt {
    @Id
    String username;
    String accessToken;
    String refreshToken;
}
