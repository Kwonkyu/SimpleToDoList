package com.simpletodolist.todolist.exception.todolist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LockedTodoListException extends RuntimeException {
    public static final String DEFAULT_ERROR = "Locked To-do List.";
    public static final String DEFAULT_MESSAGE = "To-do list is locked. Only owner can do the request.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;
}
