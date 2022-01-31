package com.simpletodolist.todolist.domains.todo.service.port;

import com.simpletodolist.todolist.domains.todo.adapter.controller.command.TodoCreateRequest;
import com.simpletodolist.todolist.domains.todo.adapter.controller.command.TodoUpdateRequest;
import com.simpletodolist.todolist.domains.todo.adapter.repository.NoTodoFoundException;
import com.simpletodolist.todolist.domains.todo.domain.Todo;
import com.simpletodolist.todolist.domains.todo.domain.Todos;

public interface TodoCrudService {

	/**
	 * Get to-dos of to-do list.
	 * @param todoListId Id of to-do list.
	 * @return To-dos information.
|	 */
	Todos getTodos(Long todoListId);

	/**
	 * Get information of to-do.
	 * @param todoId Id of to-do.
	 * @return Information of to-do.
	 * @throws NoTodoFoundException When to-do with given id not found.
	 */
	Todo getTodoInformation(Long todoId) throws NoTodoFoundException;

	/**
	 * Create to-do on to-do list.
	 * @param request To-do create request.
	 * @param username Username of to-do creating user.
	 * @return Created to-do's information.
	 */
	Todo createTodo(TodoCreateRequest request, String username);

	/**
	 * Update to-do of to-do list.
	 * @param request To-do update request.
	 * @param todoId Id of to-do.
	 * @return Updated to-do's information.
	 */
	Todo updateTodo(TodoUpdateRequest request, Long todoId);

	/**
	 * Delete to-do.
	 * @param todoId Id of to-do.
	 */
	void deleteTodo(Long todoId);

}
