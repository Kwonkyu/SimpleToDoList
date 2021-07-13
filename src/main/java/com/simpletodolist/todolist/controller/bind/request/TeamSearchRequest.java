package com.simpletodolist.todolist.controller.bind.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.controller.bind.request.field.SearchTeamField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamSearchRequest {

    @NotNull
    @JsonProperty("field")
    private SearchTeamField searchTeamField = SearchTeamField.NAME;

    @NotNull
    @JsonProperty("value")
    private Object searchValue;

    @JsonProperty("joined")
    private boolean includeJoined = false;
}
