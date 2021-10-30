package com.simpletodolist.todolist.exception.todolist;

import lombok.Getter;

@Getter
public class NoTodoListFoundException extends RuntimeException{
    public NoTodoListFoundException(long todoListId) {
        super(String.format("To-do list with id %d not found.", todoListId));
    }
}
