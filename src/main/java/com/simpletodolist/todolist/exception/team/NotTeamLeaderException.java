package com.simpletodolist.todolist.exception.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotTeamLeaderException extends RuntimeException {
    public static final String DEFAULT_ERROR = "Not Leader of Team.";
    public static final String DEFAULT_MESSAGE = "Only leader is allowed to do requested action.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;
}
