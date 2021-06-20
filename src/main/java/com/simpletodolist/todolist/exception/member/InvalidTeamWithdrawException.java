package com.simpletodolist.todolist.exception.member;

import com.simpletodolist.todolist.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvalidTeamWithdrawException extends RuntimeException{
    private String error = Member.NOT_JOINED_TEAM;
    private String message = "Try with different team identification value.";
}
