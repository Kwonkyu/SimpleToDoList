package com.simpletodolist.todolist.domains.todo.adapter.repository;

import lombok.Getter;

@Getter
public class NoTodoFoundException extends RuntimeException {

	public NoTodoFoundException(long todoId) {
		super(String.format("Todo with id %d not found.", todoId));
	}
}
