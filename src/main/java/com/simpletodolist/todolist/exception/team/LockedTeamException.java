package com.simpletodolist.todolist.exception.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LockedTeamException extends RuntimeException{
    public static final String DEFAULT_ERROR = "Locked Team.";
    public static final String DEFAULT_MESSAGE = "Team is locked. Only team leader is allowed to request given action.";

    private String error;
    private String message;
}
