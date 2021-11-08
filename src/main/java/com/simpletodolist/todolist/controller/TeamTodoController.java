package com.simpletodolist.todolist.controller;

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
@RequestMapping("/api/team/{teamId}/todolist/{todoListId}")
@RequiredArgsConstructor
public class TeamTodoController {
    private final BasicTodoService todoService;
    private final BasicAuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping("/todo")
    public ResponseEntity<List<TodoDTO>> readTodos(@PathVariable(name = "teamId") long teamId,
                                                   @PathVariable(name = "todoListId") long todoListId,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoList(teamId, username, todoListId);
        return ResponseEntity.ok(todoService.readTodosOfTodoList(todoListId));
    }

    @GetMapping("/todo/{todoId}")
    public ResponseEntity<TodoDTO> readTodo(@PathVariable(name = "teamId") long teamId,
                                            @PathVariable(name = "todoListId") long todoListId,
                                            @PathVariable(name = "todoId") long todoId,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodo(teamId, username, todoListId, todoId);
        return ResponseEntity.ok(todoService.readTodo(todoId));
    }

    @PostMapping("/todo")
    public ResponseEntity<TodoDTO> createTodo(@PathVariable(name = "teamId") long teamId,
                                              @PathVariable(name = "todoListId") long todoListId,
                                              @RequestBody @Valid TodoInformationRequest request,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoList(teamId, username, todoListId);
        TodoDTO todo = todoService.createTodo(username, todoListId, request);
        return ResponseEntity.created(URIGenerator.createTodo(teamId, todoListId, todo.getId()))
                .body(todo);
    }

    @PatchMapping("/todo/{todoId}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable(name = "teamId") long teamId,
                                              @PathVariable(name = "todoListId") long todoListId,
                                              @PathVariable(name = "todoId") long todoId,
                                              @Valid @RequestBody TodoInformationRequest request,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoUpdate(teamId, username, todoListId, todoId);
        return ResponseEntity.ok(todoService.updateTodo(todoId, request));
    }

    @DeleteMapping("/todo/{todoId}")
    public void deleteTodo(@PathVariable(name = "teamId") long teamId,
                           @PathVariable(name = "todoListId") long todoListId,
                           @PathVariable(name = "todoId") long todoId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoUpdate(teamId, username, todoListId, todoId);
        todoService.deleteTodo(todoId);
    }
}
