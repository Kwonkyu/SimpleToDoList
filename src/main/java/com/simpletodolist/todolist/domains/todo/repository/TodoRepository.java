package com.simpletodolist.todolist.domains.todo.repository;

import com.simpletodolist.todolist.domains.todo.entity.Todo;
import com.simpletodolist.todolist.domains.todolist.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByTodoList(TodoList todoList);

    Optional<Todo> findByIdAndTodoList(long id, TodoList todoList);

    boolean existsByIdAndTodoList(long id, TodoList todoList);
}
