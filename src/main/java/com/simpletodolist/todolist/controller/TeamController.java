package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.team.TeamInformationRequest;
import com.simpletodolist.todolist.controller.bind.team.TeamSearchRequest;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.BasicAuthorizationService;
import com.simpletodolist.todolist.service.team.BasicTeamService;
import com.simpletodolist.todolist.util.URIGenerator;
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
    public ResponseEntity<List<TeamDTO>> searchTeams(@Valid @RequestBody TeamSearchRequest request,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        return ResponseEntity.ok(teamService.searchTeams(request, username));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDTO> getTeamDetails(@PathVariable(name = "teamId") long teamId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.readTeam(teamId));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> registerTeam(@Valid @RequestBody TeamInformationRequest request,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        TeamDTO team = teamService.createTeam(userIdFromClaims, request);
        return ResponseEntity.created(URIGenerator.createTeam(team.getId())).body(team);
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable(name = "teamId") long teamId,
                                              @Valid @RequestBody TeamInformationRequest request,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamLeader(username, teamId);
        return ResponseEntity.ok(teamService.updateTeam(teamId, request));
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeTeam(@PathVariable(name = "teamId") long teamId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamLeader(username, teamId);
        teamService.deleteTeam(teamId);
    }
}
