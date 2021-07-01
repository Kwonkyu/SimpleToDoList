package com.simpletodolist.todolist.exception.general;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationFailedException extends RuntimeException{
    public static final String DEFAULT_ERROR = "Authentication Failed";
    public static final String DEFAULT_MESSAGE = "Provide valid credential value.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;
}
