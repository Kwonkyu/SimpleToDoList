package com.simpletodolist.todolist.domain.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Team;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeamDTO {
    @JsonProperty("id")
    private final long id;

    @JsonProperty("leaderUsername")
    private final String leaderUsername;

    @JsonProperty("alias")
    private final String alias;

    @JsonProperty("teamName")
    private final String teamName;

    @JsonProperty("todoLists")
    private final List<TodoListDTO> todoLists = new ArrayList<>();

    @JsonProperty("locked")
    private final boolean locked;


    public TeamDTO(Team team) {
        this.id = team.getId();
        this.leaderUsername = team.getLeader().getUsername();
        this.alias = team.getLeader().getAlias();
        this.teamName = team.getTeamName();
        this.locked = team.isLocked();
        team.getTodoLists().forEach(todoList -> todoLists.add(new TodoListDTO(todoList)));
    }
}
