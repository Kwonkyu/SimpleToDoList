package com.simpletodolist.todolist.domain.dto;

import com.simpletodolist.todolist.domain.UpdatableMemberInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInformationUpdateRequestDTO {

    @NotNull(message = "Updatable Field Cannot Be Empty.")
    private UpdatableMemberInformation field;

    @NotNull(message = "Updated Value Cannot Be Empty.")
    private Object value;
}
