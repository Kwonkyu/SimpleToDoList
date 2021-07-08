package com.simpletodolist.todolist.exception.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotJoinedMemberException extends RuntimeException{
    public static final String DEFAULT_ERROR = "Not Joined Member.";
    public static final String DEFAULT_MESSAGE = "Member is not joined in team.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;
}
