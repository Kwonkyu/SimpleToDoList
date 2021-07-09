package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.UpdatableTodoInformation;
import com.simpletodolist.todolist.domain.dto.TodoDTO;
import com.simpletodolist.todolist.domain.dto.TodoInformationUpdateRequestDTO;
import com.simpletodolist.todolist.domain.dto.TodosDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.AuthorizationService;
import com.simpletodolist.todolist.service.todo.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/team/{teamId}/todolist/{todoListId}")
@RequiredArgsConstructor
public class TeamTodoController {

    private final TodoService todoService;
    private final AuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    private void authorizeUntilTodoList(String memberUserId, long teamId, long todoListId) {
        authorizationService.authorizeTeamMember(memberUserId, teamId);
        authorizationService.validateTeamContainsTodoList(teamId, todoListId);
    }

    @GetMapping("/todo")
    public ResponseEntity<TodosDTO> readTodos(@PathVariable(name = "teamId") long teamId,
                                              @PathVariable(name = "todoListId") long todoListId,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizeUntilTodoList(memberUserId, teamId, todoListId);
        return ResponseEntity.ok(todoService.readTodosOfTodoList(todoListId));
    }


    @GetMapping("/todo/{todoId}")
    public ResponseEntity<TodoDTO> readTodo(@PathVariable(name = "teamId") long teamId,
                                            @PathVariable(name = "todoListId") long todoListId,
                                            @PathVariable(name = "todoId") long todoId,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizeUntilTodoList(memberUserId, teamId, todoListId);
        authorizationService.validateTodoListContainsTodo(todoListId, todoId);
        return ResponseEntity.ok(todoService.readTodo(todoId));
    }


    @PostMapping("/todo")
    public ResponseEntity<TodoDTO> createTodo(@PathVariable(name = "teamId") long teamId,
                                              @PathVariable(name = "todoListId") long todoListId,
                                              @RequestBody @Valid TodoDTO todoDTO,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizeUntilTodoList(memberUserId, teamId, todoListId);
        return ResponseEntity.ok(todoService.createTodo(memberUserId, todoListId, todoDTO));
    }

    private void authorizeTodo(String memberUserId, long teamId, long todoListId, long todoId) {
        if(todoService.isTodoLocked(todoId)) {
            authorizationService.fullAuthorization(memberUserId, teamId, todoListId, todoId);
        } // TODO: 함수형 인터페이스로.. 필요한 인증을 조합해서 전달하는 방식으로 구현할 수 있지 않을까? 아니면 인증 레벨을 분리하거나.
        else {
            authorizeUntilTodoList(memberUserId, teamId, todoListId);
            authorizationService.validateTodoListContainsTodo(todoListId, todoId);
        }
    }

    @PutMapping("/todo/{todoId}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable(name = "teamId") long teamId,
                                              @PathVariable(name = "todoListId") long todoListId,
                                              @PathVariable(name = "todoId") long todoId,
                                              @Valid @RequestBody TodoInformationUpdateRequestDTO dto,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        if(dto.getField().equals(UpdatableTodoInformation.LOCKED)) {
            authorizationService.fullAuthorization(memberUserId, teamId, todoListId, todoId);
        } else {
            authorizeTodo(memberUserId, teamId, todoListId, todoId);
        }
        return ResponseEntity.ok(todoService.updateTodo(todoId, dto.getField(), dto.getValue()));
    }

    @DeleteMapping("/todo/{todoId}")
    public void deleteTodo(@PathVariable(name = "teamId") long teamId,
                           @PathVariable(name = "todoListId") long todoListId,
                           @PathVariable(name = "todoId") long todoId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizeTodo(memberUserId, teamId, todoListId, todoId);
        todoService.deleteTodo(todoId);
    }
}
