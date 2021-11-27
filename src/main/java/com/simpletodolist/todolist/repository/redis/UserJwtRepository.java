package com.simpletodolist.todolist.repository.redis;

import com.simpletodolist.todolist.domain.entity.redis.UserJwt;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface UserJwtRepository extends KeyValueRepository<UserJwt, String> {
}
