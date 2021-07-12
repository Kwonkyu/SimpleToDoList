package com.simpletodolist.todolist.controller.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.controller.bind.TeamDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TeamsDTO {

    @JsonProperty("teams")
    private List<TeamDTO> teams = new ArrayList<>();

    public TeamsDTO(List<TeamDTO> teams) {
        this.teams = teams;
    }
}
