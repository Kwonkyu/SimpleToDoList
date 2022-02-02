package com.simpletodolist.todolist.domains.team.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Application layer presentation of team.
 */
@Getter
public class Team {

	@JsonProperty("id")
	private final Long id;

	@JsonProperty("leaderUsername")
	private final String leaderUsername;

	@JsonProperty("leaderAlias")
	private final String leaderAlias;

	@JsonProperty("teamName")
	private final String teamName;

	@JsonProperty("members")
	private final Members members;

	public Team(TeamEntity teamEntity) {
		this.id = teamEntity.getId();
		UserEntity leader = teamEntity.getLeader();
		this.leaderUsername = leader.getUsername();
		this.leaderAlias = leader.getAlias();
		this.teamName = teamEntity.getTeamName();
		this.members = new Members(
			teamEntity
				.getMembers()
				.stream()
				.map(MemberEntity::getUser)
				.collect(Collectors.toList()));
	}

}
