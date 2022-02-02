package com.simpletodolist.todolist.domains.todolist.service;

import com.simpletodolist.todolist.domains.team.adapter.repository.TeamRepository;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.todolist.adapter.controller.command.TodoListCreateRequest;
import com.simpletodolist.todolist.domains.todolist.adapter.controller.command.TodoListUpdateRequest;
import com.simpletodolist.todolist.domains.todolist.adapter.repository.NoTodoListFoundException;
import com.simpletodolist.todolist.domains.todolist.adapter.repository.TodoListRepository;
import com.simpletodolist.todolist.domains.todolist.domain.TodoList;
import com.simpletodolist.todolist.domains.todolist.domain.TodoListEntity;
import com.simpletodolist.todolist.domains.todolist.domain.TodoLists;
import com.simpletodolist.todolist.domains.todolist.service.port.TodoListAuthorizationService;
import com.simpletodolist.todolist.domains.todolist.service.port.TodoListCrudService;
import com.simpletodolist.todolist.domains.user.adapter.repository.UserRepository;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
class TodoListService implements TodoListCrudService, TodoListAuthorizationService {

	private final TeamRepository teamRepository;
	private final TodoListRepository todoListRepository;
	private final UserRepository userRepository;

	@Override
	public void checkAccessPermission(Long todoListId, String username) {
		TodoListEntity todoList = todoListRepository.findTodoListById(todoListId);
		TeamEntity team = todoList.getTeam();
		UserEntity user = userRepository.findUserByUsername(username);
		if (!team.hasMember(user)) {
			throw new AccessDeniedException("Unable to read to-do list: access denied.");
		}
	}

	@Override
	public void checkModifyPermission(Long todoListId, String username) {
		TodoListEntity todoList = todoListRepository.findTodoListById(todoListId);
		TeamEntity team = todoList.getTeam();
		UserEntity user = userRepository.findUserByUsername(username);
		if (team.getLeader()
				.equals(user)) {
			return;
		}

		if (todoList.isLocked() && !todoList.getOwner()
											.equals(user)) {
			throw new AccessDeniedException("Unable to read to-do list: access denied.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public TodoLists getTodoLists(Long teamId) {
		TeamEntity team = teamRepository.findTeamById(teamId);
		return new TodoLists(team.getTodoLists());
	}

	@Override
	@Transactional(readOnly = true)
	public TodoList getTodoListInformation(Long todoListId) throws NoTodoListFoundException {
		return new TodoList(todoListRepository.findTodoListById(todoListId));
	}

	@Override
	public TodoList createTodoList(
		TodoListCreateRequest request,
		String username
	) {
		TeamEntity team = teamRepository.findTeamById(request.getTeamId());
		UserEntity user = userRepository.findUserByUsername(username);
		return new TodoList(todoListRepository.save(
			TodoListEntity.builder()
						  .owner(user)
						  .team(team)
						  .name(request.getTodoListName())
						  .locked(request.isLocked())
						  .build()));
	}

	@Override
	public TodoList updateTodoList(
		Long todoListId,
		TodoListUpdateRequest request
	) {
		TodoListEntity todoList = todoListRepository.findTodoListById(todoListId);
		todoList.changeName(request.getTodoListName());
		todoList.changeLocked(request.isLocked());
		return new TodoList(todoList);
	}

	@Override
	public void deleteTodoList(
		Long todoListId
	) {
		TodoListEntity todoList = todoListRepository.findTodoListById(todoListId);
		TeamEntity team = todoList.getTeam();
		team.getTodoLists()
			.remove(todoList);
		todoList.getTodos()
				.clear();
		todoListRepository.delete(todoList);
	}
}
