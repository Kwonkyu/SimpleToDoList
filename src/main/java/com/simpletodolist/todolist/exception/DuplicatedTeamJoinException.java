package com.simpletodolist.todolist.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DuplicatedTeamJoinException extends RuntimeException{

    private String message = "Already Joined Team.";
}
