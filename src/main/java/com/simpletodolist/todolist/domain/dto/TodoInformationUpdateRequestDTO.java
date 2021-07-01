package com.simpletodolist.todolist.domain.dto;

import com.simpletodolist.todolist.domain.UpdatableTodoInformation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TodoInformationUpdateRequestDTO {

    @NotNull(message = "Updatable Field Cannot Be Empty.")
    private UpdatableTodoInformation field;

    @NotNull(message = "Updated Value Cannot Be Empty.")
    private Object value;
}
