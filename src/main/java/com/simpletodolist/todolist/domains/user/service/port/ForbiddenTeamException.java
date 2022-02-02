package com.simpletodolist.todolist.domains.user.service.port;

import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import lombok.Getter;

@Getter
public class ForbiddenTeamException extends RuntimeException {

	public ForbiddenTeamException(TeamEntity teamEntity) {
		super(String.format("Unable to join team %s: team is locked.", teamEntity.getTeamName()));
	}
}
