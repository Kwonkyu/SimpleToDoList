package com.simpletodolist.todolist.exception.todolist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoTodoListFoundException extends RuntimeException{
    private String message = "No TodoList Found";
    private String solution = "Try with different to-do list identification value.";
}
