package com.simpletodolist.todolist.exception.team;

import lombok.Getter;

@Getter
public class NoTeamFoundException extends RuntimeException{
    public NoTeamFoundException(long teamId) {
        super(String.format("Team with id %d not found.", teamId));
    }
}
