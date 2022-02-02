package com.simpletodolist.todolist.domains.team.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import lombok.Getter;


/**
 * Application layer presentation of user registration on team.
 */
@Getter
public class Member {

	@JsonProperty("id")
	private final Long id;

	@JsonProperty("username")
	private final String username;

	@JsonProperty("alias")
	private final String alias;

	public Member(UserEntity userEntity) {
		this.id = userEntity.getId();
		this.username = userEntity.getUsername();
		this.alias = userEntity.getAlias();
	}
}
