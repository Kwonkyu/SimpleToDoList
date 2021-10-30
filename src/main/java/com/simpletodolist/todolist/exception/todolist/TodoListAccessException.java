package com.simpletodolist.todolist.exception.todolist;

import com.simpletodolist.todolist.domain.entity.TodoList;
import lombok.Getter;

@Getter
public class TodoListAccessException extends RuntimeException{
    public TodoListAccessException(TodoList todoList) {
        super(String.format("Access to to-do list %s denied.", todoList.getName()));
    }
}
