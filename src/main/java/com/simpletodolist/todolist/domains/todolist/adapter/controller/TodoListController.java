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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team/{teamId}/todolist")
@RequiredArgsConstructor
public class TodoListController {

	private final TodoListCrudService crudService;
	private final TeamAuthorizationService teamAuthorizationService;
	private final TodoListAuthorizationService todoListAuthorizationService;

	@GetMapping
	public ResponseEntity<ApiResponse<TodoLists>> getTeamTodoLists(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId
	) {
		teamAuthorizationService.checkMemberAccess(teamId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.getTodoLists(teamId)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<TodoList>> createTodoList(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId,
		@RequestBody @Valid TodoListCreateRequest request
	) {
		teamAuthorizationService.checkMemberAccess(teamId, authentication.getName());
		TodoList created = crudService.createTodoList(teamId, request, authentication.getName());
		return ResponseEntity
			.created(URI.create(String.format("/api/team/%d/todolist/%d", teamId, created.getId())))
			.body(ApiResponse.success(created));
	}

	@GetMapping("/{todoListId}")
	public ResponseEntity<ApiResponse<TodoList>> getTodoList(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId,
		@PathVariable(name = "todoListId") long todoListId
	) {
		teamAuthorizationService.checkMemberAccess(teamId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.getTodoListInformation(teamId, todoListId)));
	}

	@PutMapping("/{todoListId}")
	public ResponseEntity<ApiResponse<TodoList>> updateTodoList(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId,
		@PathVariable(name = "todoListId") long todoListId,
		@Valid @RequestBody TodoListUpdateRequest request
	) {
		todoListAuthorizationService.checkOwnerAccess(teamId, todoListId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.updateTodoList(teamId, todoListId, request)));
	}

	@DeleteMapping("/{todoListId}")
	public ResponseEntity<Object> deleteTodoList(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId,
		@PathVariable(name = "todoListId") long todoListId
	) {
		todoListAuthorizationService.checkOwnerAccess(teamId, todoListId, authentication.getName());
		crudService.deleteTodoList(teamId, todoListId);
		return ResponseEntity.noContent()
							 .build();
	}
}
