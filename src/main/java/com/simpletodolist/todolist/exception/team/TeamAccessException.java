package com.simpletodolist.todolist.exception.team;

import com.simpletodolist.todolist.domain.entity.Team;
import lombok.Getter;

@Getter
public class TeamAccessException extends RuntimeException{
    public TeamAccessException(Team team) {
        super(String.format("Access to team %s is denied. Please contact leader.", team.getTeamName()));
    }
}
