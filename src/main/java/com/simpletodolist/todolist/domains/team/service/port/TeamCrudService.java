package com.simpletodolist.todolist.domains.team.service.port;

import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamCreateRequest;
import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamUpdateRequest;
import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamSearchRequest;
import com.simpletodolist.todolist.domains.team.domain.SearchedTeams;
import com.simpletodolist.todolist.domains.team.domain.Team;

public interface TeamCrudService {

	/**
	 * Get team's information including member list.
	 * @param teamId Id of team.
	 * @return Team object containing team's information.
	 */
	Team getTeamInformation(Long teamId);

	/**
	 * Search team's based on request.
	 * @param request Search request command including search field, value, include-joined, paging.
	 * @param username Searching user's username.
	 * @return Searched team's information with pagination info.
	 */
	SearchedTeams searchTeams(TeamSearchRequest request, String username);

	/**
	 * Create new team.
	 * @param request Create command including team's name.
	 * @param leaderUsername Creating user's username. Automatically given leader role.
	 * @return Created team's information.
	 */
	Team createTeam(TeamCreateRequest request, String leaderUsername);

	/**
	 * Update team's information.
	 * @param teamId Id of team.
	 * @param request Update command including team's name, lock status.
	 * @return Updated team's information.
	 */
	Team updateTeam(Long teamId, TeamUpdateRequest request);

	/**
	 * Delete team including to-do lists, to-dos.
	 * @param teamId Id of team.
	 */
	void deleteTeam(Long teamId);

}
