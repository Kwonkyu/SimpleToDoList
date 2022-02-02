package com.simpletodolist.todolist.domains.todo.adapter.controller.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class TodoUpdateRequest {

	@NotBlank
	@Length(max = 64)
	@JsonProperty("title")
	private String title;

	@NotBlank
	@Length(max = 1024)
	@JsonProperty("content")
	private String content;

	@JsonProperty("locked")
	private boolean locked;
}
