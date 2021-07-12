package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.request.field.UpdatableTodoListInformation;
import com.simpletodolist.todolist.controller.bind.TodoListDTO;
import com.simpletodolist.todolist.controller.bind.request.TodoListInformationUpdateRequest;
import com.simpletodolist.todolist.controller.bind.TodoListsDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.AuthorizationService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.service.todolist.TodoListService;
import com.simpletodolist.todolist.util.URIGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/team/{teamId}")
@RequiredArgsConstructor
public class TeamTodoListController {

    private final TeamService teamService;
    private final TodoListService todoListService;
    private final AuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    private void authorizeTodoList(String memberUserId, long teamId, long todoListId) {
        if (todoListService.isTodoListLocked(todoListId)) {
            authorizationService.fullAuthorization(memberUserId, teamId, todoListId);
        } else {
            authorizationService.authorizeTeamMember(memberUserId, teamId);
            authorizationService.validateTeamContainsTodoList(teamId, todoListId);
        }
    }

    @GetMapping("/todolist")
    public ResponseEntity<TodoListsDTO> getTeamTodoLists(@PathVariable(name = "teamId") long teamId,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(memberUserId, teamId);
        return ResponseEntity.ok(teamService.getTeamTodoLists(teamId));
    }

    @PostMapping("/todolist")
    public ResponseEntity<TodoListDTO> createTodoListOnTeam(@PathVariable(name = "teamId") long teamId,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                                                            @RequestBody @Valid TodoListDTO todoListDTO){
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(memberUserId, teamId);
        TodoListDTO createdTodoList = todoListService.createTodoList(teamId, memberUserId, todoListDTO);
        return ResponseEntity.created(URIGenerator.createTodoList(teamId, createdTodoList.getTodoListId())).body(createdTodoList);
    }

    @GetMapping("/todolist/{todoListId}")
    public ResponseEntity<TodoListDTO> getTodoList(@PathVariable(name = "teamId") long teamId,
                                                   @PathVariable(name = "todoListId") long todoListId,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(memberUserId, teamId);
        return ResponseEntity.ok(todoListService.getTodoListDetail(todoListId));
    }

    @PatchMapping("/todolist/{todoListId}")
    public ResponseEntity<TodoListDTO> updateTodoList(@PathVariable(name = "teamId") long teamId,
                                                      @PathVariable(name = "todoListId") long todoListId,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                                                      // TODO: check HttpMessageNotReadableException where using enums.
                                                      @Valid @RequestBody TodoListInformationUpdateRequest dto) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        if(dto.getField().equals(UpdatableTodoListInformation.LOCKED)) {
            authorizationService.fullAuthorization(memberUserId, teamId, todoListId);
        } else {
            authorizeTodoList(memberUserId, teamId, todoListId);
        }

        return ResponseEntity.ok(todoListService.updateTodoList(todoListId, dto.getField(), dto.getValue()));
    }

    @DeleteMapping("/todolist/{todoListId}")
    public void deleteTodoList(@PathVariable(name = "teamId") long teamId,
                               @PathVariable(name = "todoListId") long todoListId,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizeTodoList(memberUserId, teamId, todoListId);
        todoListService.deleteTodoList(todoListId);
    }

}
