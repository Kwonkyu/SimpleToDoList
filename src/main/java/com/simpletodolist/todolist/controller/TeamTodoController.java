package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.controller.bind.todo.TodoInformationRequest;
import com.simpletodolist.todolist.domain.bind.TodoDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.BasicAuthorizationService;
import com.simpletodolist.todolist.service.todo.BasicTodoService;
import com.simpletodolist.todolist.util.URIGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/team/{teamId}/todolist/{todoListId}/todo")
@RequiredArgsConstructor
public class TeamTodoController {
    private final BasicTodoService todoService;
    private final BasicAuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping
    public ResponseEntity<ApiResponse<List<TodoDTO>>> readTodos(@PathVariable(name = "teamId") long teamId,
                                                                @PathVariable(name = "todoListId") long todoListId,
                                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoList(teamId, username, todoListId);
        return ResponseEntity.ok(ApiResponse.success(todoService.readTodosOfTodoList(todoListId)));
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<ApiResponse<TodoDTO>> readTodo(@PathVariable(name = "teamId") long teamId,
                                                         @PathVariable(name = "todoListId") long todoListId,
                                                         @PathVariable(name = "todoId") long todoId,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodo(teamId, username, todoListId, todoId);
        return ResponseEntity.ok(ApiResponse.success(todoService.readTodo(todoId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TodoDTO>> createTodo(@PathVariable(name = "teamId") long teamId,
                                                           @PathVariable(name = "todoListId") long todoListId,
                                                           @RequestBody @Valid TodoInformationRequest request,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoList(teamId, username, todoListId);
        TodoDTO todo = todoService.createTodo(username, todoListId, request);
        return ResponseEntity.created(URIGenerator.createTodo(teamId, todoListId, todo.getId()))
                .body(ApiResponse.success(todo));
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<ApiResponse<TodoDTO>> updateTodo(@PathVariable(name = "teamId") long teamId,
                                                           @PathVariable(name = "todoListId") long todoListId,
                                                           @PathVariable(name = "todoId") long todoId,
                                                           @Valid @RequestBody TodoInformationRequest request,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        if (request.isLocked()) {
            authorizationService.authorizeTodoLock(teamId, username, todoListId, todoId);
        } else {
            authorizationService.authorizeTodoUpdate(teamId, username, todoListId, todoId);
        }
        return ResponseEntity.ok(ApiResponse.success(todoService.updateTodo(todoId, request)));
    }

    @DeleteMapping("/{todoId}")
    public void deleteTodo(@PathVariable(name = "teamId") long teamId,
                           @PathVariable(name = "todoListId") long todoListId,
                           @PathVariable(name = "todoId") long todoId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoUpdate(teamId, username, todoListId, todoId);
        todoService.deleteTodo(todoId);
    }
}
