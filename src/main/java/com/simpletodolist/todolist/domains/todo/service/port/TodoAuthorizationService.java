package com.simpletodolist.todolist.domains.todo.service.port;

import org.springframework.security.access.AccessDeniedException;

public interface TodoAuthorizationService {


	/**
	 * Check whether user can update to-do.
	 *
	 * @param todoId Id of to-do.
	 * @param username Username of user.
	 * @throws AccessDeniedException when user is not permitted to update to-do.
	 */
	void checkAccessPermission(Long todoId, String username)
		throws AccessDeniedException;

	/**
	 * Check whether user can update to-do.
	 *
	 * @param todoId Id of to-do.
	 * @param username Username of user.
	 * @throws AccessDeniedException when user is not permitted to update to-do.
	 */
	void checkModifyPermission(Long todoId, String username)
		throws AccessDeniedException;
}
