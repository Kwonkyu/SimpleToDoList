package com.simpletodolist.todolist.domain.dto;

import com.simpletodolist.todolist.domain.UpdatableTeamInformation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TeamInformationUpdateRequestDTO {

    @NotNull(message = "Updatable Field Cannot Be Empty.")
    private UpdatableTeamInformation field;

    @NotNull(message = "Updated Value Cannot Be Empty.")
    private Object value;
}
