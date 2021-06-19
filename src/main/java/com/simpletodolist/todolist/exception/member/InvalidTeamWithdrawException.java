package com.simpletodolist.todolist.exception.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvalidTeamWithdrawException extends RuntimeException{
    private String message = "Not Joined Team.";
    private String solution = "Try with different team identification value.";
}
