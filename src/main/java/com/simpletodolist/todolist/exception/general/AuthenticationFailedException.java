package com.simpletodolist.todolist.exception.general;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationFailedException extends RuntimeException{
    private String error = "Authentication failed.";
    private String message = "Provide valid authentication value.";
}
