package com.simpletodolist.todolist.exception.member;

import lombok.Getter;

@Getter
public class LoginFailedException extends RuntimeException{
    public LoginFailedException(String userId, String cause) {
        super(String.format("Unable to login account %s: '%s'.", userId, cause));
    }
}
