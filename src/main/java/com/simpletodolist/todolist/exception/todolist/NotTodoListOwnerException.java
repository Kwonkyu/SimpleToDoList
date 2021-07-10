package com.simpletodolist.todolist.exception.todolist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotTodoListOwnerException extends RuntimeException{
    public static final String DEFAULT_ERROR = "Not Owner of To-do List.";
    public static final String DEFAULT_MESSAGE = "Only owner of to-do list is allowed to request given action.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;
}
