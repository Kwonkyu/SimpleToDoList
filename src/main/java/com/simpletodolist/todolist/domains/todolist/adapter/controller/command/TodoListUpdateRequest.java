package com.simpletodolist.todolist.domains.todolist.adapter.controller.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class TodoListUpdateRequest {

	@NotBlank(message = "할 일 리스트의 이름은 비워둘 수 없습니다.")
	@Length(max = 64, message = "할 일 리스트의 이름은 64 글자를 초과할 수 없습니다.")
	@JsonProperty("name")
	private String todoListName;

    @NotNull
	@JsonProperty("locked")
	private boolean locked;
}
