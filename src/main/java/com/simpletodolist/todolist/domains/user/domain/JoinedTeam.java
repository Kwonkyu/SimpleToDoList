package com.simpletodolist.todolist.domains.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import lombok.Getter;

@Getter
public class JoinedTeam {

	@JsonProperty("id")
	private final long id;

	@JsonProperty("leaderUsername")
	private final String leaderUsername;

	@JsonProperty("leaderAlias")
	private final String leaderAlias;

	@JsonProperty("teamName")
	private final String teamName;

	@JsonProperty("locked")
	private final boolean locked;

	public JoinedTeam(TeamEntity team) {
		this.id = team.getId();
		UserEntity leader = team.getLeader();
		this.leaderUsername = leader.getUsername();
		this.leaderAlias = leader.getAlias();
		this.teamName = team.getTeamName();
		this.locked = team.isLocked();
	}

}
