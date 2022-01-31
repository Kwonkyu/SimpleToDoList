package com.simpletodolist.todolist.domains.todo.adapter.controller.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class TodoCreateRequest {

	@NotNull
	@JsonProperty("teamId")
	private long teamId;

	@NotNull
	@JsonProperty("todoListId")
	private long todoListId;

	@NotBlank
	@Length(max = 64)
	@JsonProperty("title")
	private String title;

	@NotBlank
	@Length(max = 1024)
	@JsonProperty("content")
	private String content;

	@NotNull
	@JsonProperty("locked")
	private boolean locked;
}
