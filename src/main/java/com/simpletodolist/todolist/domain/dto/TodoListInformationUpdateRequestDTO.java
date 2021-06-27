package com.simpletodolist.todolist.domain.dto;

import com.simpletodolist.todolist.domain.UpdatableTodoListInformation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TodoListInformationUpdateRequestDTO {
    @NotNull
    UpdatableTodoListInformation field;

    @NotNull
    Object value;
}
