package com.simpletodolist.todolist.domains.todolist.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.todo.bind.response.TodoDTO;
import com.simpletodolist.todolist.domains.todolist.entity.TodoList;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TodoListDTO {
    @JsonProperty("id")
    private final long id;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("alias")
    private final String alias;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("todos")
    private final List<TodoDTO> todos = new ArrayList<>();

    @JsonProperty("locked")
    private final boolean locked;


    public TodoListDTO(TodoList todoList) {
        this.id = todoList.getId();
        this.username = todoList.getOwner().getUsername();
        this.alias = todoList.getOwner().getAlias();
        this.name = todoList.getName();
        this.locked = todoList.isLocked();
        todoList.getTodos().forEach(todo -> todos.add(new TodoDTO(todo)));
    }
}
