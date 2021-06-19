package com.simpletodolist.todolist.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatedTeamException extends RuntimeException{
    private String message = "Already Existing Team.";
}
