package com.simpletodolist.todolist.exception.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoTodoFoundException extends RuntimeException {
    private String message = "No Todo Found.";
    private String solution = "Try with different member identification value.";
}
