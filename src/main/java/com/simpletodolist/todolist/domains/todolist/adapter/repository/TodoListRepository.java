package com.simpletodolist.todolist.domains.todolist.adapter.repository;

import com.simpletodolist.todolist.domains.todolist.domain.TodoListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoListRepository extends JpaRepository<TodoListEntity, Long> {

    default TodoListEntity findTodoListById(Long id) {
        return findById(id).orElseThrow(() -> new NoTodoListFoundException(id));
    }
}
