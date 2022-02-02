package com.simpletodolist.todolist.domains.invitation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import lombok.Getter;

/**
 * Application layer presentation of member-invited user.
 */
@Getter
public class InvitedUser {

	@JsonProperty("id")
	private final long id;

	@JsonProperty("username")
	private final String username;

	public InvitedUser(UserEntity userEntity) {
		this.id = userEntity.getId();
		this.username = userEntity.getUsername();
	}
}
