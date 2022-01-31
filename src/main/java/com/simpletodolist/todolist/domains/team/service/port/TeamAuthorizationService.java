package com.simpletodolist.todolist.domains.team.service.port;

import org.springframework.security.access.AccessDeniedException;

public interface TeamAuthorizationService {

	/**
	 * Check whether not-joined 3rd user can access team.
	 * @param teamId Id of team.
	 * @param joinedUsername Username of user.
	 * @throws AccessDeniedException when user cannot access team.
	 */
	void checkPublicAccess(Long teamId, String joinedUsername) throws AccessDeniedException;

	/**
	 * Check whether user is leader of team.
	 * @param teamId Id of team.
	 * @param leaderUsername Username of user.
	 * @throws AccessDeniedException when user is not leader of team.
	 */
	void checkLeaderPermission(Long teamId, String leaderUsername) throws AccessDeniedException;

	/**
	 * Check whether user is member of team.
	 * @param teamId Id of team.
	 * @param memberUsername Username of user.
	 * @throws AccessDeniedException when user is not member of team.
	 */
	void checkMemberPermission(Long teamId, String memberUsername) throws AccessDeniedException;
}
