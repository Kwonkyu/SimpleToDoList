package com.simpletodolist.todolist.controller.bind.todolist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TodoListInformationRequest {
    @NotBlank(message = "할 일 리스트의 이름은 비워둘 수 없습니다.")
    @Length(max = 64, message = "할 일 리스트의 이름은 64 글자를 초과할 수 없습니다.")
    @JsonProperty("name")
    private String todoListName;

    @JsonProperty("locked")
    private boolean locked;
}
