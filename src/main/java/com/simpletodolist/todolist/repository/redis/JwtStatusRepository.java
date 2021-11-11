package com.simpletodolist.todolist.repository.redis;

import com.simpletodolist.todolist.domain.entity.redis.JwtStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtStatusRepository extends CrudRepository<JwtStatus, String> {
    Optional<JwtStatus> findByUsername(String username);
}
