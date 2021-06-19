package com.simpletodolist.todolist.exception.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatedTeamJoinException extends RuntimeException{
    private String message = "Already Joined Team.";
    private String solution = "Try join different team.";
}
