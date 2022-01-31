package com.simpletodolist.todolist.domains.todolist.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.team.service.port.TeamAuthorizationService;
import com.simpletodolist.todolist.domains.todolist.adapter.controller.command.TodoListCreateRequest;
import com.simpletodolist.todolist.domains.todolist.adapter.controller.command.TodoListUpdateRequest;
import com.simpletodolist.todolist.domains.todolist.domain.TodoList;
import com.simpletodolist.todolist.domains.todolist.domain.TodoLists;
import com.simpletodolist.todolist.domains.todolist.service.port.TodoListAuthorizationService;
import com.simpletodolist.todolist.domains.todolist.service.port.TodoListCrudService;
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
@RequestMapping("/api/todoLists")
@RequiredArgsConstructor
public class TodoListController {

	private final TodoListCrudService crudService;
	private final TeamAuthorizationService teamAuthService;
	private final TodoListAuthorizationService todoListAuthorizationService;

	@GetMapping
	public ResponseEntity<ApiResponse<TodoLists>> getTeamTodoLists(
		@AuthenticationPrincipal Authentication authentication,
		@RequestParam("team") long teamId
	) {
		teamAuthService.checkMemberPermission(teamId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.getTodoLists(teamId)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<TodoList>> createTodoList(
		@AuthenticationPrincipal Authentication authentication,
		@RequestBody @Valid TodoListCreateRequest request
	) {
		teamAuthService.checkMemberPermission(request.getTeamId(), authentication.getName());
		TodoList created = crudService.createTodoList(request, authentication.getName());
		return ResponseEntity
			.created(URI.create(String.format("/api/todolist/%d", created.getId())))
			.body(ApiResponse.success(created));
	}

	@GetMapping("/{todoListId}")
	public ResponseEntity<ApiResponse<TodoList>> getTodoList(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "todoListId") long todoListId
	) {
		todoListAuthorizationService.checkAccessPermission(todoListId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.getTodoListInformation(todoListId)));
	}

	@PutMapping("/{todoListId}")
	public ResponseEntity<ApiResponse<TodoList>> updateTodoList(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable("todoListId") long todoListId,
		@Valid @RequestBody TodoListUpdateRequest request
	) {
		todoListAuthorizationService.checkModifyPermission(todoListId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.updateTodoList(todoListId, request)));
	}

	@DeleteMapping("/{todoListId}")
	public ResponseEntity<Object> deleteTodoList(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "todoListId") long todoListId
	) {
		todoListAuthorizationService.checkModifyPermission(todoListId, authentication.getName());
		crudService.deleteTodoList(todoListId);
		return ResponseEntity.noContent()
							 .build();
	}
}
