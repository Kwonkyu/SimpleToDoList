package com.simpletodolist.todolist.util.container;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TestTodoListDTO {
    @JsonProperty("id")
    private long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("name")
    private  String name;

    @JsonProperty("todos")
    private List<TestTodoDTO> todos;

    @JsonProperty("locked")
    private boolean locked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TestTodoDTO> getTodos() {
        return todos;
    }

    public void setTodos(List<TestTodoDTO> todos) {
        this.todos = todos;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
