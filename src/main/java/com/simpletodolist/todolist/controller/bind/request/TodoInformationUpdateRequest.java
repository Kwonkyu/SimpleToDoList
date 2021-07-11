package com.simpletodolist.todolist.controller.bind.request;

import com.simpletodolist.todolist.domain.UpdatableTodoInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoInformationUpdateRequest {

    @NotNull(message = "Updatable Field Cannot Be Empty.")
    private UpdatableTodoInformation field;

    @NotNull(message = "Updated Value Cannot Be Empty.")
    private Object value;
}
