package com.simpletodolist.todolist.domains.todo.service;

import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.todo.adapter.controller.command.TodoCreateRequest;
import com.simpletodolist.todolist.domains.todo.adapter.controller.command.TodoUpdateRequest;
import com.simpletodolist.todolist.domains.todo.adapter.repository.NoTodoFoundException;
import com.simpletodolist.todolist.domains.todo.adapter.repository.TodoRepository;
import com.simpletodolist.todolist.domains.todo.domain.Todo;
import com.simpletodolist.todolist.domains.todo.domain.TodoEntity;
import com.simpletodolist.todolist.domains.todo.domain.Todos;
import com.simpletodolist.todolist.domains.todo.service.port.TodoAuthorizationService;
import com.simpletodolist.todolist.domains.todo.service.port.TodoCrudService;
import com.simpletodolist.todolist.domains.todolist.adapter.repository.TodoListRepository;
import com.simpletodolist.todolist.domains.todolist.domain.TodoListEntity;
import com.simpletodolist.todolist.domains.user.adapter.repository.UserRepository;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
class TodoService implements TodoCrudService, TodoAuthorizationService {

	private final TodoListRepository todoListRepository;
	private final TodoRepository todoRepository;
	private final UserRepository userRepository;

	@Override
	public void checkAccessPermission(Long todoId, String username) throws AccessDeniedException {
		UserEntity user = userRepository.findUserByUsername(username);
		TodoEntity todo = todoRepository.findTodoById(todoId);
		TodoListEntity todoList = todo.getTodoList();
		TeamEntity team = todoList.getTeam();

		if (team.isLocked() && !team.hasMember(user)) {
			throw new AccessDeniedException(
				"Unable to access to-do: user not joined to locked team.");
		}
	}

	@Override
	public void checkModifyPermission(Long todoId, String username) throws AccessDeniedException {
		UserEntity user = userRepository.findUserByUsername(username);
		TodoEntity todo = todoRepository.findTodoById(todoId);
		TodoListEntity todoList = todo.getTodoList();
		TeamEntity team = todoList.getTeam();

		if (!todo.isLocked()) {
			return;
		}

		if (!user.equals(todoList.getOwner()) && !user.equals(team.getLeader())) {
			throw new AccessDeniedException(
				"Unable to modify to-do: user not permitted to modify to-do.");
		}
	}

	@Override
	public Todos getTodos(Long todoListId) {
		TodoListEntity todoList = todoListRepository.findTodoListById(todoListId);
		return new Todos(todoList.getTodos());
	}

	@Override
	public Todo getTodoInformation(Long todoId) throws NoTodoFoundException {
		return new Todo(todoRepository.findTodoById(todoId));
	}

	@Override
	public Todo createTodo(TodoCreateRequest request, String username) {
		UserEntity user = userRepository.findUserByUsername(username);
		TodoListEntity todoList = todoListRepository.findTodoListById(request.getTodoListId());
		return new Todo(todoRepository.save(
			TodoEntity.builder()
					  .title(request.getTitle())
					  .content(request.getContent())
					  .todoList(todoList)
					  .locked(request.isLocked())
					  .writer(user)
					  .build()));
	}

	@Override
	public Todo updateTodo(TodoUpdateRequest request, Long todoId) {
		TodoEntity todo = todoRepository.findTodoById(todoId);
		todo.changeTitle(request.getTitle());
		todo.changeContent(request.getContent());
		return new Todo(todo);
	}

	@Override
	public void deleteTodo(Long todoId) {
		TodoEntity todo = todoRepository.findTodoById(todoId);
		TodoListEntity todoList = todo.getTodoList();
		todoList.getTodos()
				.remove(todo);
		todoRepository.delete(todo);
	}
}
