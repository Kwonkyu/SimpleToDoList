package com.simpletodolist.todolist.exception.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationFailedException extends RuntimeException{
    public static final String DEFAULT_ERROR = "Authorization Failed.";
    public static final String DEFAULT_MESSAGE = "Provide valid authentication value.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;
}
