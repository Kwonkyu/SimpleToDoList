package com.simpletodolist.todolist.repository;

import com.simpletodolist.todolist.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
