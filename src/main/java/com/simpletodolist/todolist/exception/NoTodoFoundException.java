package com.simpletodolist.todolist.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoTodoFoundException extends RuntimeException {

    private String message = "No Todo Found.";
}
