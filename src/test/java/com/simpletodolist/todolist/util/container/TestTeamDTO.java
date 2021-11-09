package com.simpletodolist.todolist.util.container;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.bind.TodoListDTO;

import java.util.List;

public class TestTeamDTO {
    @JsonProperty("id")
    private long id;

    @JsonProperty("leaderUsername")
    private String leaderUsername;

    @JsonProperty("leaderAlias")
    private String alias;

    @JsonProperty("teamName")
    private String teamName;

    @JsonProperty("todoLists")
    private List<TodoListDTO> todoLists;

    @JsonProperty("locked")
    private boolean locked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLeaderUsername() {
        return leaderUsername;
    }

    public void setLeaderUsername(String leaderUsername) {
        this.leaderUsername = leaderUsername;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<TodoListDTO> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(List<TodoListDTO> todoLists) {
        this.todoLists = todoLists;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
