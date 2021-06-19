package com.simpletodolist.todolist.exception.member;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoMemberFoundException extends RuntimeException{
    private String message = "No Member Found.";
    private String solution = "Try with different member identification value.";
}
