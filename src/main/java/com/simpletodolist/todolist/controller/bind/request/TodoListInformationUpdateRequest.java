package com.simpletodolist.todolist.controller.bind.request;

import com.simpletodolist.todolist.domain.UpdatableTodoListInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoListInformationUpdateRequest {

    @NotNull(message = "Updatable Field Cannot Be Empty.")
    private UpdatableTodoListInformation field;

    @NotNull(message = "Updated Value Cannot Be Empty.")
    private Object value;
}
