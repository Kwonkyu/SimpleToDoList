package com.simpletodolist.todolist.exception.general;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationFailedException extends RuntimeException{
    private String message = "Authentication failed.";
    private String solution = "Provide valid authentication value.";
}
