package com.simpletodolist.todolist.domain.dto;

import com.simpletodolist.todolist.domain.UpdatableTodoListInformation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TodoListInformationUpdateRequestDTO {

    @NotNull(message = "Updatable Field Cannot Be Empty.")
    private UpdatableTodoListInformation field;

    @NotNull(message = "Updated Value Cannot Be Empty.")
    private Object value;
}
