package com.simpletodolist.todolist.domains.todo.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.team.service.port.TeamAuthorizationService;
import com.simpletodolist.todolist.domains.todo.adapter.controller.command.TodoCreateRequest;
import com.simpletodolist.todolist.domains.todo.adapter.controller.command.TodoUpdateRequest;
import com.simpletodolist.todolist.domains.todo.domain.Todo;
import com.simpletodolist.todolist.domains.todo.domain.Todos;
import com.simpletodolist.todolist.domains.todo.service.port.TodoAuthorizationService;
import com.simpletodolist.todolist.domains.todo.service.port.TodoCrudService;
import com.simpletodolist.todolist.domains.todolist.service.port.TodoListAuthorizationService;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

	private final TodoCrudService todoService;
	private final TodoAuthorizationService todoAuthorizationService;
	private final TeamAuthorizationService teamAuthorizationService;
	private final TodoListAuthorizationService todoListAuthorizationService;

	@GetMapping
	public ResponseEntity<ApiResponse<Todos>> readTodos(
		@AuthenticationPrincipal Authentication authentication,
		@RequestParam(name = "todoList") long todoListId
	) {
		todoListAuthorizationService.checkAccessPermission(todoListId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			todoService.getTodos(todoListId)));
	}

	@GetMapping("/{todoId}")
	public ResponseEntity<ApiResponse<Todo>> readTodo(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "todoId") long todoId
	) {
		todoAuthorizationService.checkAccessPermission(todoId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			todoService.getTodoInformation(todoId)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Todo>> createTodo(
		@AuthenticationPrincipal Authentication authentication,
		@RequestBody @Valid TodoCreateRequest request
	) {
		teamAuthorizationService.checkMemberPermission(
			request.getTeamId(), authentication.getName());
		Todo todo = todoService.createTodo(request, authentication.getName());
		return ResponseEntity
			.created(URI.create("/api/todos/" + todo.getId()))
			.body(ApiResponse.success(todo));
	}

	@PutMapping("/{todoId}")
	public ResponseEntity<ApiResponse<Todo>> updateTodo(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "todoId") long todoId,
		@Valid @RequestBody TodoUpdateRequest request
	) {
		todoAuthorizationService.checkModifyPermission(todoId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			todoService.updateTodo(request, todoId)));
	}

	@DeleteMapping("/{todoId}")
	public ResponseEntity<Object> deleteTodo(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "todoId") long todoId
	) {
		todoAuthorizationService.checkModifyPermission(todoId, authentication.getName());
		todoService.deleteTodo(todoId);
		return ResponseEntity.noContent()
							 .build();
	}
}
