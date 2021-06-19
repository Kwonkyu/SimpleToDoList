package com.simpletodolist.todolist.exception.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatedMemberException extends RuntimeException{
    private String message = "Already Existing Member.";
    private String solution = "Try with different user identification value.";
}
