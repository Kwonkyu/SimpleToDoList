package com.simpletodolist.todolist.domains.user.service.port;

import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import lombok.Getter;

@Getter
public class IllegalTeamRequestException extends IllegalArgumentException {

	public IllegalTeamRequestException(
		TeamEntity teamEntity,
		UserEntity userEntity,
		boolean joined
	) {
		super(String.format(
			"Member %s %s joined team %s.",
			userEntity.getUsername(),
			joined ? "already" : "not",
			teamEntity.getTeamName()
		));
	}
}
