package com.simpletodolist.todolist.domains.todolist.service.port;

import com.simpletodolist.todolist.domains.todolist.adapter.controller.command.TodoListCreateRequest;
import com.simpletodolist.todolist.domains.todolist.adapter.controller.command.TodoListUpdateRequest;
import com.simpletodolist.todolist.domains.todolist.adapter.repository.NoTodoListFoundException;
import com.simpletodolist.todolist.domains.todolist.domain.TodoList;
import com.simpletodolist.todolist.domains.todolist.domain.TodoLists;

/**
 * The interface To-do list crud service.
 */
public interface TodoListCrudService {

	/**
	 * Get to-do lists of team.
	 * @param teamId Id of team.
	 * @return To-do lists information.
	 */
	TodoLists getTodoLists(Long teamId);

	/**
	 * Gets to-do list information.
	 *
	 * @param todoListId id of to-do list
	 * @return to-do list information
	 * @throws NoTodoListFoundException when no to-do list found.
	 */
	TodoList getTodoListInformation(Long todoListId) throws NoTodoListFoundException;

	/**
	 * Create to-do list.
	 *
	 * @param request  to-do list create request.
	 * @param username username of creating to-do list.
	 * @return information of created to-do list.
	 */
	TodoList createTodoList(TodoListCreateRequest request, String username);

	/**
	 * Update to-do list.
	 *
	 * @param todoListId id of to-do list.
	 * @param request    to-do list update request.
	 * @return information of updated to-do list.
	 */
	TodoList updateTodoList(
		Long todoListId,
		TodoListUpdateRequest request
	);

	/**
	 * Delete to-do list.
	 *
	 * @param todoListId id of to-do list.
	 */
	void deleteTodoList(Long todoListId);

}
