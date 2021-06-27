package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.TodoListDTO;
import com.simpletodolist.todolist.domain.dto.TodoListInformationUpdateRequestDTO;
import com.simpletodolist.todolist.domain.dto.TodoListsDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.service.todolist.TodoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/team/{teamId}")
@RequiredArgsConstructor
public class TeamTodoListController {

    private final TeamService teamService;
    private final TodoListService todoListService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping("/todolist")
    public ResponseEntity<TodoListsDTO> getTeamTodoLists(@PathVariable(name = "teamId") long teamId,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        return ResponseEntity.ok(todoListService.getTodoListsOfTeam(teamId));
    }

    @PostMapping("/todolist")
    public ResponseEntity<TodoListDTO> createTodoListOnTeam(@PathVariable(name = "teamId") long teamId,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                                                            @RequestBody TodoListDTO todoListDTO,
                                                            HttpServletRequest request){
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        TodoListDTO createdTodoList = todoListService.createTodoList(teamId, memberUserId, todoListDTO);
        HttpHeaders headers = new HttpHeaders();
        // TODO: apply hateoas to other 'POST' requests.
        headers.set(HttpHeaders.LOCATION, request.getRequestURL().append(String.format("/%d", createdTodoList.getTodoListId())).toString());
        return new ResponseEntity<>(createdTodoList, headers, HttpStatus.OK);
    }

    @GetMapping("/todolist/{todoListId}")
    public ResponseEntity<TodoListDTO> getTodoList(@PathVariable(name = "teamId") long teamId,
                                                   @PathVariable(name = "todoListId") long todoListId,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        return ResponseEntity.ok(todoListService.getTodoListDetail(todoListId));
    }

    @PutMapping("/todolist/{todoListId}")
    public ResponseEntity<TodoListDTO> updateTodoList(@PathVariable(name = "teamId") long teamId,
                                                      @PathVariable(name = "todoListId") long todoListId,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                                                      // TODO: resolve HttpMessageNotReadableException where using enums.
                                                      @RequestBody TodoListInformationUpdateRequestDTO dto) {
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        todoListService.authorizeMember(memberUserId, todoListId);
        return ResponseEntity.ok(todoListService.updateTodoList(todoListId, dto.getField(), dto.getValue()));
    }

    @DeleteMapping("/todolist/{todoListId}")
    public void deleteTodoList(@PathVariable(name = "teamId") long teamId,
                               @PathVariable(name = "todoListId") long todoListId,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        // TODO: make it as filter?
        String memberUserId = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(memberUserId, teamId);
        todoListService.authorizeMember(memberUserId, todoListId);
        todoListService.deleteTodoList(todoListId);
    }



}
