package com.simpletodolist.todolist.exception.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotWriterTodoException extends RuntimeException{
    public static final String DEFAULT_ERROR = "Not Writer of To-do.";
    public static final String DEFAULT_MESSAGE = "Only writer of to-do is allowed to request given action.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;
}
