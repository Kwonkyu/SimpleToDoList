package com.simpletodolist.todolist.domains.todolist.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.todo.domain.Todo;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Application layer presentation of to-do list.
 */
@Getter
public class TodoList {

	@JsonProperty("id")
	private final long id;

	@JsonProperty("username")
	private final String username;

	@JsonProperty("alias")
	private final String alias;

	@JsonProperty("name")
	private final String name;

	@JsonProperty("todos")
	private final List<Todo> todos;

	@JsonProperty("locked")
	private final boolean locked;


	public TodoList(TodoListEntity todoList) {
		this.id = todoList.getId();
		UserEntity owner = todoList.getOwner();
		this.username = owner.getUsername();
		this.alias = owner.getAlias();
		this.name = todoList.getName();
		this.locked = todoList.isLocked();
		this.todos = todoList.getTodos()
							 .stream()
							 .map(Todo::new)
							 .collect(Collectors.toList());
	}
}
