package com.simpletodolist.todolist.domains.team.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import lombok.Getter;

/**
 * Application layer presentation of searched team.
 */
@Getter
public class SearchedTeam {

	@JsonProperty("id")
	private final Long id;

	@JsonProperty("leaderUsername")
	private final String leaderUsername;

	@JsonProperty("leaderAlias")
	private final String leaderAlias;

	@JsonProperty("teamName")
	private final String teamName;

	public SearchedTeam(TeamEntity teamEntity) {
		this.id = teamEntity.getId();
		UserEntity leader = teamEntity.getLeader();
		this.leaderUsername = leader.getUsername();
		this.leaderAlias = leader.getAlias();
		this.teamName = teamEntity.getTeamName();
	}

}
