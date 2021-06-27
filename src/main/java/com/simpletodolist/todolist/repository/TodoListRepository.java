package com.simpletodolist.todolist.repository;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.domain.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    List<TodoList> findAllByOwner(Member owner);
    List<TodoList> findAllByTeam(Team team);
}
