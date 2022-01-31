package com.simpletodolist.todolist.domains.team.adapter.controller.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamSearchRequest {

	@NotNull(message = "탐색 항목은 비워둘 수 없습니다.")
	@JsonProperty("field")
	private SearchField searchField;

	@NotBlank(message = "검색 값은 비워둘 수 없습니다.")
	@JsonProperty("value")
	private String searchValue;

	@JsonProperty("joined")
	private boolean includeJoined;

	@JsonProperty("page")
	private int page;

	@JsonProperty("size")
	private int size;

	public enum SearchField {
		NAME, LEADER,
	}
}