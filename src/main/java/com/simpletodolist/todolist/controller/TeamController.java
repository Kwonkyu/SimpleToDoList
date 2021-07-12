package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.TeamDTO;
import com.simpletodolist.todolist.controller.bind.TeamsDTO;
import com.simpletodolist.todolist.controller.bind.request.TeamInformationUpdateRequest;
import com.simpletodolist.todolist.controller.bind.request.TeamSearchRequest;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.AuthorizationService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.util.URIGenerator;
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
    private final AuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping
    public ResponseEntity<TeamsDTO> searchTeams(@Valid @RequestBody TeamSearchRequest request,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        // TODO: jwt를 계속 파싱하는 것도 번거로운데 필터같은걸로 파싱해서 메서드로 전달해줄 수 있나?
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        return ResponseEntity.ok(teamService.searchTeams(request.getSearchTeamField(), request.getSearchValue(), userIdFromClaims, request.isIncludeJoined()));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDTO> getTeamDetails(@PathVariable(name = "teamId") long teamId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.getTeamDetails(teamId));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> registerTeam(@Valid @RequestBody TeamDTO teamDTO,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        TeamDTO team = teamService.createTeam(userIdFromClaims, teamDTO);
        return ResponseEntity.created(URIGenerator.createTeam(team.getId())).body(team);
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable(name = "teamId") long teamId,
                                              @Valid @RequestBody TeamInformationUpdateRequest dto,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.updateTeam(teamId, dto.getField(), dto.getValue()));
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeTeam(@PathVariable(name = "teamId") long teamId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        teamService.deleteTeam(teamId);
    }


}
