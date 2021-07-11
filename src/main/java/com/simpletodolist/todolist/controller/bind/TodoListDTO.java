package com.simpletodolist.todolist.controller.bind;

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

    // TODO: pick these json property string to public static final Strings.
    @JsonProperty("id")
    private long todoListId;

    @JsonProperty("ownerUserId")
    private String ownerUserId;

    @NotBlank
    @JsonProperty("name")
    private String todoListName;

    @JsonProperty("todos")
    private List<TodoDTO> todos = new ArrayList<>();

    @JsonProperty("locked")
    private boolean locked;

    public TodoListDTO(TodoList todoList) {
        this.todoListId = todoList.getId();
        this.ownerUserId = todoList.getOwner().getUserId();
        this.todoListName = todoList.getName();
        this.todos = todoList.getTodos().stream().map(TodoDTO::new).collect(Collectors.toList());
        this.locked = todoList.isLocked();
    }
}
