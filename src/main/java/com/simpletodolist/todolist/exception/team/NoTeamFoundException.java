package com.simpletodolist.todolist.exception.team;

import com.simpletodolist.todolist.domain.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoTeamFoundException extends RuntimeException{
    private String error = Team.NO_TEAM_FOUND;
    private String message = "Try with different team identification value.";
}
