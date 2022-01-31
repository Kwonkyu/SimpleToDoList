package com.simpletodolist.todolist.domains.todo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Application layer presentation of to-dos.
 */
@Getter
public class Todos {

	@JsonProperty("todos")
	private final List<Todo> todoList;

	public Todos(Collection<TodoEntity> todoEntities) {
		this.todoList = todoEntities.stream()
								 .map(Todo::new)
								 .collect(Collectors.toList());
	}

}
