package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.TodoDTO;
import com.simpletodolist.todolist.domain.dto.TodoInformationUpdateRequestDTO;
import com.simpletodolist.todolist.domain.dto.TodosDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.service.todo.TodoService;
import com.simpletodolist.todolist.service.todolist.TodoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/team/{teamId}/todolist/{todoListId}")
@RequiredArgsConstructor
public class TeamTodoController {

    private final TeamService teamService;
    private final TodoService todoService;
    private final TodoListService todoListService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping("/todo")
    public ResponseEntity<TodosDTO> readTodos(@PathVariable(name = "teamId") long teamId,
                                              @PathVariable(name = "todoListId") long todoListId,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        return ResponseEntity.ok(todoService.readTodosOfTodoList(todoListId));
    }


    @GetMapping("/todo/{todoId}")
    public ResponseEntity<TodoDTO> readTodo(@PathVariable(name = "teamId") long teamId,
                                            @PathVariable(name = "todoListId") long todoListId,
                                            @PathVariable(name = "todoId") long todoId,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        todoListService.authorizeMember(memberUserId, todoListId);
        return ResponseEntity.ok(todoService.readTodo(todoId));
    }

    @PostMapping("/todo")
    public ResponseEntity<TodoDTO> createTodo(@PathVariable(name = "teamId") long teamId,
                                              @PathVariable(name = "todoListId") long todoListId,
                                              @RequestBody TodoDTO todoDTO,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        todoListService.authorizeMember(memberUserId, todoListId);
        return ResponseEntity.ok(todoService.writeTodo(memberUserId, todoListId, todoDTO));
    }

    @PutMapping("/todo/{todoId}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable(name = "teamId") long teamId,
                                              @PathVariable(name = "todoListId") long todoListId,
                                              @PathVariable(name = "todoId") long todoId,
                                              @Valid @RequestBody TodoInformationUpdateRequestDTO dto,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        todoListService.authorizeMember(memberUserId, todoListId);
        todoService.authorizeMember(memberUserId, todoId);
        return ResponseEntity.ok(todoService.updateTodo(todoId, dto.getField(), dto.getValue()));
    }

    @DeleteMapping("/todo/{todoId}")
    public void deleteTodo(@PathVariable(name = "teamId") long teamId,
                           @PathVariable(name = "todoListId") long todoListId,
                           @PathVariable(name = "todoId") long todoId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        todoListService.authorizeMember(memberUserId, todoListId);
        todoService.authorizeMember(memberUserId, todoId);
        todoService.deleteTodo(todoId);
    }
}
