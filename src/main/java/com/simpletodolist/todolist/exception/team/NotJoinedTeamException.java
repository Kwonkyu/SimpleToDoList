package com.simpletodolist.todolist.exception.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotJoinedTeamException extends RuntimeException {
    public static final String DEFAULT_ERROR = "Not Joined Team.";
    public static final String DEFAULT_MESSAGE = "Cannot access not joined team.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;
}
