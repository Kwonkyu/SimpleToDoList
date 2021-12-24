package com.simpletodolist.todolist.domains.team.controller;

import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.team.bind.request.TeamInformationRequest;
import com.simpletodolist.todolist.domains.team.bind.request.TeamSearchRequest;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.common.util.JwtTokenUtil;
import com.simpletodolist.todolist.common.service.BasicAuthorizationService;
import com.simpletodolist.todolist.domains.team.service.BasicTeamService;
import com.simpletodolist.todolist.common.util.URIGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {
    private final BasicTeamService teamService;
    private final BasicAuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamDTO>>> searchTeams(@Valid @RequestBody TeamSearchRequest request,
                                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        return ResponseEntity.ok(ApiResponse.success(teamService.searchTeams(request, username)));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamDTO>> getTeamDetails(@PathVariable(name = "teamId") long teamId,
                                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamMember(teamId, username);
        return ResponseEntity.ok(ApiResponse.success(teamService.readTeam(teamId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TeamDTO>> registerTeam(@Valid @RequestBody TeamInformationRequest request,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        TeamDTO team = teamService.createTeam(username, request);
        return ResponseEntity.created(URIGenerator.createTeam(team.getId()))
                .body(ApiResponse.success(team));
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamDTO>> updateTeam(@PathVariable(name = "teamId") long teamId,
                                                           @Valid @RequestBody TeamInformationRequest request,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamLeader(teamId, username);
        return ResponseEntity.ok(ApiResponse.success(teamService.updateTeam(teamId, request)));
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeTeam(@PathVariable(name = "teamId") long teamId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamLeader(teamId, username);
        teamService.deleteTeam(teamId);
    }
}
