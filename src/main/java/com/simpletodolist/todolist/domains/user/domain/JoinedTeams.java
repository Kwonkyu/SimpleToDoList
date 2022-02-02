package com.simpletodolist.todolist.domains.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class JoinedTeams {

	@JsonProperty("teams")
	private final List<JoinedTeam> teams;

	public JoinedTeams(List<TeamEntity> teams) {
		this.teams = teams.stream()
						  .map(JoinedTeam::new)
						  .collect(Collectors.toList());
	}

}
