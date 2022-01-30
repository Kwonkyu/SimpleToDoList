package com.simpletodolist.todolist.domains.user.service.port;

import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserRegisterRequest;
import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserUpdateRequest;
import com.simpletodolist.todolist.domains.user.domain.User;

public interface UserCrudService {

	/**
	 * Get member details.
	 * @param username Member's username.
	 * @return Member's information.
	 */
	User getMemberDetails(String username);

	/**
	 * Register new member.
	 * @param request Member create command object.
	 * @return Created member's information.
	 */
	User registerMember(UserRegisterRequest request);

	/**
	 * Update member.
	 * @param username Member's username.
	 * @param request Member update command object.
	 * @return Updated member's information.
	 */
	User updateMember(String username, UserUpdateRequest request);


	void withdrawMember(String username);
}
