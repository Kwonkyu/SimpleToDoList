package com.simpletodolist.todolist.domains.team.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

/**
 * Application layer presentation of searched teams.
 */
@Getter
public class SearchedTeams {

	@JsonProperty("page")
	private final int page;

	@JsonProperty("size")
	private final int size;

	@JsonProperty("pages")
	private final int pages;

	@JsonProperty("teams")
	private final List<SearchedTeam> teams;

	public SearchedTeams(Page<TeamEntity> searchedTeams) {
		this.pages = searchedTeams.getTotalPages();
		this.page = searchedTeams.getNumber();
		this.size = searchedTeams.getSize();
		this.teams = searchedTeams.map(SearchedTeam::new)
								  .toList();
	}

}
