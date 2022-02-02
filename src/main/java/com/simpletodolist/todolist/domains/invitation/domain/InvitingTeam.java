package com.simpletodolist.todolist.domains.invitation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import lombok.Getter;

/**
 * Application layer presentation of member inviting team.
 */
@Getter
public class InvitingTeam {

	@JsonProperty("id")
	private final long id;

	@JsonProperty("name")
	private final String teamName;

	public InvitingTeam(TeamEntity teamEntity) {
		this.id = teamEntity.getId();
		this.teamName = teamEntity.getTeamName();
	}
}
