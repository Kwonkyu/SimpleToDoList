package com.simpletodolist.todolist.domains.jwt.repository;

import com.simpletodolist.todolist.domains.jwt.UserJwt;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface UserJwtRepository extends KeyValueRepository<UserJwt, String> {
}
