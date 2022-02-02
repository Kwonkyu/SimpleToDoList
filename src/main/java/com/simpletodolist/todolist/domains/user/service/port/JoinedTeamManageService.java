package com.simpletodolist.todolist.domains.user.service.port;

import com.simpletodolist.todolist.domains.user.domain.JoinedTeam;
import com.simpletodolist.todolist.domains.user.domain.JoinedTeams;

public interface JoinedTeamManageService {

	/**
	 * Join team.
	 * @param teamId Team's id.
	 * @param username Member's username.
	 * @return Joined team's information.
	 * @throws IllegalTeamRequestException when member already joined team.
	 * @throws ForbiddenTeamException when member not authorized to join team.
	 */
	JoinedTeam joinTeam(Long teamId, String username)
		throws IllegalTeamRequestException, ForbiddenTeamException;

	/**
	 * Withdraw team.
	 * @param teamId Team's id.
	 * @param username Member's username.
	 * @throws IllegalTeamRequestException when member not joined team.
	 */
	void withdrawTeam(Long teamId, String username)
		throws IllegalTeamRequestException;

	/**
	 * Get joined teams of member.
	 * @param username Member's username.
	 * @return Information of joined teams.
	 */
	JoinedTeams getJoinedTeams(String username);
}
