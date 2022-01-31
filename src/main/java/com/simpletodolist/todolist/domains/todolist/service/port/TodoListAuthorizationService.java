package com.simpletodolist.todolist.domains.todolist.service.port;

public interface TodoListAuthorizationService {

	/**
	 * Check if user can read to-do list.
	 *
	 * @param todoListId the id of to-do list.
	 * @param username   the username of accessing user.
	 */
	void checkAccessPermission(Long todoListId, String username);

	/**
	 * Check if user can update, delete to-do list.
	 *
	 * @param todoListId the id of to-do list.
	 * @param username   the username of user.
	 */
	void checkModifyPermission(Long todoListId, String username);
}
