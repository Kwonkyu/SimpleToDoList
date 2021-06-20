package com.simpletodolist.todolist.exception.member;

import com.simpletodolist.todolist.domain.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatedTeamJoinException extends RuntimeException{
    private String error = Member.DUPLICATED_TEAM_JOINED;
    private String message = "Try join different team.";
}
