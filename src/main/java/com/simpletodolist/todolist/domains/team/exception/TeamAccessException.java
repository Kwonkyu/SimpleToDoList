package com.simpletodolist.todolist.domains.team.exception;

import com.simpletodolist.todolist.domains.team.entity.Team;
import lombok.Getter;

@Getter
public class TeamAccessException extends RuntimeException{
    public TeamAccessException(Team team) {
        super(String.format("Access to team %s is denied. Please contact leader.", team.getTeamName()));
    }
}
