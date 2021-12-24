package com.simpletodolist.todolist.domains.jwt.repository;

import com.simpletodolist.todolist.domains.jwt.JwtStatus;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JwtStatusRepository extends KeyValueRepository<JwtStatus, String> {
}
