package com.simpletodolist.todolist.domains.user.adapter.repository;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{

	public UserNotFoundException(String username) {
		super(String.format("Member with username %s not found.", username));
	}
}
