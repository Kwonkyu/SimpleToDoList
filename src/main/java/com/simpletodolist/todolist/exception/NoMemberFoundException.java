package com.simpletodolist.todolist.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoMemberFoundException extends RuntimeException{

    private String message = "No Member Found.";
}
