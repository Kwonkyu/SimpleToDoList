package com.simpletodolist.todolist.domains.todolist.service.port;

public interface TodoListAuthorizationService {

	/**
	 * Check if owner access is needed and if so permitted to user on to-do list..
	 *
	 * @param teamId     the id of team.
	 * @param todoListId the id of to-do list.
	 * @param username   the username of accessing user.
	 */
	void checkOwnerAccess(Long teamId, Long todoListId, String username);
}
