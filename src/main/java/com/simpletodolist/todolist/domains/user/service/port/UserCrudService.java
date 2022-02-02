package com.simpletodolist.todolist.domains.user.service.port;

import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserRegisterRequest;
import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserUpdateRequest;
import com.simpletodolist.todolist.domains.user.domain.User;

public interface UserCrudService {

	/**
	 * Get user details.
	 *
	 * @param username user's username.
	 * @return user's information.
	 */
	User getUserDetails(String username);

	/**
	 * Register new user.
	 *
	 * @param request user create command object.
	 * @return Created user's information.
	 */
	User registerUser(UserRegisterRequest request);

	/**
	 * Update user.
	 *
	 * @param username user's username.
	 * @param request  user update command object.
	 * @return Updated user's information.
	 */
	User updateUser(String username, UserUpdateRequest request);

	/**
	 * Withdraw user.
	 *
	 * @param username user's username.
	 */
	void withdrawUser(String username);
}
