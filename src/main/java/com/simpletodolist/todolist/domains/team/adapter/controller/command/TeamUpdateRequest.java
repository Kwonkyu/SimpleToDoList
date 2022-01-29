package com.simpletodolist.todolist.domains.team.adapter.controller.command;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamUpdateRequest {

	@NotBlank(message = "팀 이름은 비워둘 수 없습니다.")
	private String teamName;

	@NotNull(message = "잠금 상태는 null 일 수 없습니다.")
	private boolean locked;
}
