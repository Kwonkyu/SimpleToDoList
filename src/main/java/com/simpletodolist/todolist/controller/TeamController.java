package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDTO> getTeamDetails(@PathVariable(name = "teamId") long teamId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.getTeamDetails(teamId));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> registerTeam(@Valid @RequestBody TeamDTO teamDTO,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        return ResponseEntity.ok(teamService.createTeam(userIdFromClaims, teamDTO));
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeTeam(@PathVariable(name = "teamId") long teamId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamLeader(userIdFromClaims, teamId);
        teamService.deleteTeam(teamId);
    }


}
