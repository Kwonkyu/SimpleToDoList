package com.simpletodolist.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.TodoList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TodoListDTO {

    @JsonProperty("id")
    private long todoListId;

    @JsonProperty("ownerUserId")
    private String ownerId;

    @NotBlank
    @JsonProperty("todoListName")
    private String todoListName;

    @JsonProperty("todos")
    private List<TodoDTO> todos = new ArrayList<>();

    public TodoListDTO(TodoList todoList) {
        this.todoListId = todoList.getId();
        this.ownerId = todoList.getOwner().getUserId();
        this.todoListName = todoList.getName();
        this.todos = todoList.getTodos().stream().map(TodoDTO::new).collect(Collectors.toList());
    }
}
