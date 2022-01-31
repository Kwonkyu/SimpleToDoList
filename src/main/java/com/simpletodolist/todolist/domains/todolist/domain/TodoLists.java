package com.simpletodolist.todolist.domains.todolist.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Application layer presentation of to-do lists.
 */
@Getter
public class TodoLists {

	@JsonProperty("todoLists")
	private final List<TodoList> todoListList;

	public TodoLists(List<TodoListEntity> todoListEntities) {
		this.todoListList = todoListEntities
			.stream()
			.map(TodoList::new)
			.collect(Collectors.toList());
	}
}
