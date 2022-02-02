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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(name = "todoList") long todoListId
	) {
		todoListAuthorizationService.checkAccessPermission(todoListId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			todoService.getTodos(todoListId)));
	}

	@GetMapping("/{todoId}")
	public ResponseEntity<ApiResponse<Todo>> readTodo(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable(name = "todoId") long todoId
	) {
		todoAuthorizationService.checkAccessPermission(todoId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			todoService.getTodoInformation(todoId)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Todo>> createTodo(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody @Valid TodoCreateRequest request
	) {
		teamAuthorizationService.checkMemberPermission(
			request.getTeamId(), userDetails.getUsername());
		Todo todo = todoService.createTodo(request, userDetails.getUsername());
		return ResponseEntity
			.created(URI.create("/api/todos/" + todo.getId()))
			.body(ApiResponse.success(todo));
	}

	@PutMapping("/{todoId}")
	public ResponseEntity<ApiResponse<Todo>> updateTodo(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable(name = "todoId") long todoId,
		@Valid @RequestBody TodoUpdateRequest request
	) {
		todoAuthorizationService.checkModifyPermission(todoId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			todoService.updateTodo(request, todoId)));
	}

	@DeleteMapping("/{todoId}")
	public ResponseEntity<Object> deleteTodo(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable(name = "todoId") long todoId
	) {
		todoAuthorizationService.checkModifyPermission(todoId, userDetails.getUsername());
		todoService.deleteTodo(todoId);
		return ResponseEntity.noContent()
							 .build();
	}
}
