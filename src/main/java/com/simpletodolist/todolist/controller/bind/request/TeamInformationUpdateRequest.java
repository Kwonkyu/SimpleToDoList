package com.simpletodolist.todolist.controller.bind.request;

import com.simpletodolist.todolist.domain.UpdatableTeamInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamInformationUpdateRequest {

    @NotNull(message = "Updatable Field Cannot Be Empty.")
    private UpdatableTeamInformation field;

    @NotNull(message = "Updated Value Cannot Be Empty.")
    private Object value;
}
