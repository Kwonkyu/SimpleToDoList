package com.simpletodolist.todolist.domains.team.adapter.controller.command;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamCreateRequest {

	@NotBlank(message = "팀 이름은 비워둘 수 없습니다.")
	private String teamName;
}
