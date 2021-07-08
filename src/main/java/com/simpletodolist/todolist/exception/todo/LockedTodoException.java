package com.simpletodolist.todolist.exception.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LockedTodoException extends RuntimeException{
    public static final String DEFAULT_ERROR = "Locked Todo.";
    public static final String DEFAULT_MESSAGE = "Todo is locked. Only writer is allowed to request given action.";

    private String error;
    private String message;
}
