package com.simpletodolist.todolist.domains.team.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.todolist.bind.TodoListDTO;
import com.simpletodolist.todolist.domains.team.entity.Team;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeamDTO {
    @JsonProperty("id")
    private final long id;

    @JsonProperty("leaderUsername")
    private final String leaderUsername;

    @JsonProperty("leaderAlias")
    private final String leaderAlias;

    @JsonProperty("teamName")
    private final String teamName;

    @JsonProperty("todoLists")
    private final List<TodoListDTO> todoLists = new ArrayList<>();

    @JsonProperty("locked")
    private final boolean locked;


    public TeamDTO(Team team) {
        this.id = team.getId();
        this.leaderUsername = team.getLeader().getUsername();
        this.leaderAlias = team.getLeader().getAlias();
        this.teamName = team.getTeamName();
        this.locked = team.isLocked();
        team.getTodoLists().forEach(todoList -> todoLists.add(new TodoListDTO(todoList)));
    }
}