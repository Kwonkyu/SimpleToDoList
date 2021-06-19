package com.simpletodolist.todolist.exception.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatedMemberJoinException extends RuntimeException{
    private String message = "Already Joined Member.";
    private String solution = "Try with different member.";
}
