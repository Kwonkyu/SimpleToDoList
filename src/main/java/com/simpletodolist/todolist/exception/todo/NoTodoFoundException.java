package com.simpletodolist.todolist.exception.todo;

import lombok.Getter;

@Getter
public class NoTodoFoundException extends RuntimeException {
    public NoTodoFoundException(long todoId) {
        super(String.format("Todo with id %d not found.", todoId));
    }
}
