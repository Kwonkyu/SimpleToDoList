package com.simpletodolist.todolist.exception.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LockedMemberException extends RuntimeException{
    public static final String DEFAULT_ERROR = "Locked Account.";
    public static final String DEFAULT_MESSAGE = "Account is locked. Ask manager.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;

}
