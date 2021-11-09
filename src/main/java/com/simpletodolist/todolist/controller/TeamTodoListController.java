package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.controller.bind.todolist.TodoListInformationRequest;
import com.simpletodolist.todolist.domain.bind.TodoListDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.BasicAuthorizationService;
import com.simpletodolist.todolist.service.todolist.BasicTodoListService;
import com.simpletodolist.todolist.util.URIGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/team/{teamId}")
@RequiredArgsConstructor
public class TeamTodoListController {
    private final BasicTodoListService todoListService;
    private final BasicAuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping("/todolist")
    public ResponseEntity<ApiResponse<List<TodoListDTO>>> getTeamTodoLists(@PathVariable(name = "teamId") long teamId,
                                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamMember(teamId, username);
        return ResponseEntity.ok(ApiResponse.success(todoListService.listTodoList(teamId)));
    }

    @PostMapping("/todolist")
    public ResponseEntity<ApiResponse<TodoListDTO>> createTodoListOnTeam(@PathVariable(name = "teamId") long teamId,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                                                            @RequestBody @Valid TodoListInformationRequest request){
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamMember(teamId, username);
        TodoListDTO createdTodoList = todoListService.createTodoList(teamId, username, request);
        return ResponseEntity.created(URIGenerator.createTodoList(teamId, createdTodoList.getId()))
                .body(ApiResponse.success(createdTodoList));
    }

    @GetMapping("/todolist/{todoListId}")
    public ResponseEntity<ApiResponse<TodoListDTO>> getTodoList(@PathVariable(name = "teamId") long teamId,
                                                   @PathVariable(name = "todoListId") long todoListId,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoList(teamId, username, todoListId);
        return ResponseEntity.ok(ApiResponse.success(todoListService.getTodoListDetail(todoListId)));
    }

    @PatchMapping("/todolist/{todoListId}")
    public ResponseEntity<ApiResponse<TodoListDTO>> updateTodoList(@PathVariable(name = "teamId") long teamId,
                                                      @PathVariable(name = "todoListId") long todoListId,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                                                      @Valid @RequestBody TodoListInformationRequest request) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoListUpdate(teamId, username, todoListId);
        return ResponseEntity.ok(ApiResponse.success(todoListService.updateTodoList(todoListId, request)));
    }

    @DeleteMapping("/todolist/{todoListId}")
    public void deleteTodoList(@PathVariable(name = "teamId") long teamId,
                               @PathVariable(name = "todoListId") long todoListId,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTodoListUpdate(teamId, username, todoListId);
        todoListService.deleteTodoList(todoListId);
    }
}
