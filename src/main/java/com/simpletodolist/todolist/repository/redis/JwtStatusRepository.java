package com.simpletodolist.todolist.repository.redis;

import com.simpletodolist.todolist.domain.entity.redis.JwtStatus;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JwtStatusRepository extends KeyValueRepository<JwtStatus, String> {
}
