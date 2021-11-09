package com.simpletodolist.todolist.domain.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.TodoList;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
