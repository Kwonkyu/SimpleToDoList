package com.simpletodolist.todolist.exception.todo;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Todo;
import lombok.Getter;

@Getter
public class TodoAccessException extends RuntimeException {
    public TodoAccessException(Member member, Todo todo) {
        super(String.format("User %s is not allowed to access to-do '%s'.", member.getAlias(), todo.getTitle()));
    }
}
