package com.simpletodolist.todolist.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TodoListCreateDTO {
    @JsonProperty("name")
    public String todoListName;

    public TodoListCreateDTO(String todoListName) {
        this.todoListName = todoListName;
    }
}
