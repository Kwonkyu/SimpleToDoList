package com.simpletodolist.todolist.domains.todolist.service.port;

public interface TodoListAuthorizationService {

	/**
	 * Check owner access(member of team, owner of to-do list or leader of team) of to-do list..
	 *
	 * @param teamId     the id of team.
	 * @param todoListId the id of to-do list.
	 * @param username   the username of accessing user.
	 */
	void checkOwnerAccess(Long teamId, Long todoListId, String username);
}
