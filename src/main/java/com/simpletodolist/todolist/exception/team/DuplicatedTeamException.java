package com.simpletodolist.todolist.exception.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatedTeamException extends RuntimeException{
    private String message = "Already Existing Team.";
    private String solution = "Try with different team identification value.";
}
