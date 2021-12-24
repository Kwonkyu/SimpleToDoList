package com.simpletodolist.todolist.domains.todolist.exception;

import com.simpletodolist.todolist.domains.todolist.entity.TodoList;
import lombok.Getter;

@Getter
public class TodoListAccessException extends RuntimeException{
    public TodoListAccessException(TodoList todoList) {
        super(String.format("Access to to-do list %s denied.", todoList.getName()));
    }
}
