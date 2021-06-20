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
public class DuplicatedMemberJoinException extends RuntimeException{
    private String error = Team.DUPLICATED_MEMBER_JOINED;
    private String message = "Try with different member.";
}
