package com.simpletodolist.todolist.domains.todo.bind.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.todo.entity.Todo;
import lombok.Getter;

@Getter
public class TodoDTO {
    @JsonProperty("id")
    private final long id;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("alias")
    private final String alias;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("content")
    private final String content;

    @JsonProperty("locked")
    private final boolean locked;


    public TodoDTO(Todo todo) {
        this.id = todo.getId();
        this.username = todo.getWriter().getUsername();
        this.alias = todo.getWriter().getAlias();
        this.title = todo.getTitle();
        this.content = todo.getContent();
        this.locked = todo.isLocked();
    }
}
