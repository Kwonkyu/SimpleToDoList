package com.simpletodolist.todolist.exception.todo;

import com.simpletodolist.todolist.domain.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoTodoFoundException extends RuntimeException {
    private String error = Todo.NO_TODO_FOUND;
    private String message = "Try with different member identification value.";
}
