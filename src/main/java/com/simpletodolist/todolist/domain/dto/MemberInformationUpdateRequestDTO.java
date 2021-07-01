package com.simpletodolist.todolist.domain.dto;

import com.simpletodolist.todolist.domain.UpdatableMemberInformation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MemberInformationUpdateRequestDTO {

    @NotNull(message = "Updatable Field Cannot Be Empty.")
    private UpdatableMemberInformation field;

    @NotNull(message = "Updated Value Cannot Be Empty.")
    private Object value;
}
