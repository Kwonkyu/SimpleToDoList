package com.simpletodolist.todolist.domains.team.adapter.repository;

import lombok.Getter;

@Getter
public class TeamNotFoundException extends RuntimeException{

	public TeamNotFoundException(Long id) {
		super(String.format("Team with id %d not found.", id));
	}
}
