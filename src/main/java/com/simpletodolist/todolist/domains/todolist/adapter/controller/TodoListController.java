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
@RequestMapping("/api/todoLists")
@RequiredArgsConstructor
public class TodoListController {

	private final TodoListCrudService crudService;
	private final TeamAuthorizationService teamAuthService;
	private final TodoListAuthorizationService todoListAuthorizationService;

	@GetMapping
	public ResponseEntity<ApiResponse<TodoLists>> getTeamTodoLists(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam("team") long teamId
	) {
		teamAuthService.checkMemberPermission(teamId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.getTodoLists(teamId)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<TodoList>> createTodoList(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody @Valid TodoListCreateRequest request
	) {
		teamAuthService.checkMemberPermission(request.getTeamId(), userDetails.getUsername());
		TodoList created = crudService.createTodoList(request, userDetails.getUsername());
		return ResponseEntity
			.created(URI.create(String.format("/api/todolist/%d", created.getId())))
			.body(ApiResponse.success(created));
	}

	@GetMapping("/{todoListId}")
	public ResponseEntity<ApiResponse<TodoList>> getTodoList(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable(name = "todoListId") long todoListId
	) {
		todoListAuthorizationService.checkAccessPermission(todoListId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.getTodoListInformation(todoListId)));
	}

	@PutMapping("/{todoListId}")
	public ResponseEntity<ApiResponse<TodoList>> updateTodoList(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable("todoListId") long todoListId,
		@Valid @RequestBody TodoListUpdateRequest request
	) {
		todoListAuthorizationService.checkModifyPermission(todoListId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.updateTodoList(todoListId, request)));
	}

	@DeleteMapping("/{todoListId}")
	public ResponseEntity<Object> deleteTodoList(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable(name = "todoListId") long todoListId
	) {
		todoListAuthorizationService.checkModifyPermission(todoListId, userDetails.getUsername());
		crudService.deleteTodoList(todoListId);
		return ResponseEntity.noContent()
							 .build();
	}
}
