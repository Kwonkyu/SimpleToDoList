package com.simpletodolist.todolist.domains.todo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import lombok.Getter;


/**
 * Application layer presentation of to-do.
 */
@Getter
public class Todo {

	@JsonProperty("id")
	private final long id;

	@JsonProperty("username")
	private final String username;

	@JsonProperty("alias")
	private final String alias;

	@JsonProperty("title")
	private final String title;

	@JsonProperty("content")
	private final String content;

	@JsonProperty("locked")
	private final boolean locked;


	public Todo(TodoEntity todo) {
		this.id = todo.getId();
        UserEntity writer = todo.getWriter();
        this.username = writer.getUsername();
		this.alias = writer.getAlias();
		this.title = todo.getTitle();
		this.content = todo.getContent();
		this.locked = todo.isLocked();
	}
}
