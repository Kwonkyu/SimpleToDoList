package com.simpletodolist.todolist.domains.todolist.exception;

import lombok.Getter;

@Getter
public class NoTodoListFoundException extends RuntimeException{
    public NoTodoListFoundException(long todoListId) {
        super(String.format("To-do list with id %d not found.", todoListId));
    }
}
