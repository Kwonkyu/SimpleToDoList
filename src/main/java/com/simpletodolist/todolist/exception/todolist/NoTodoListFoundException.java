package com.simpletodolist.todolist.exception.todolist;

import com.simpletodolist.todolist.domain.entity.TodoList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoTodoListFoundException extends RuntimeException{
    private String error = TodoList.NO_TODOLIST_FOUND;
    private String message = "Try with different to-do list identification value.";
}
