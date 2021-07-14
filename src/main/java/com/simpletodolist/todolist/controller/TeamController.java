package com.simpletodolist.todolist.controller;

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
import java.util.List;

import static com.simpletodolist.todolist.domain.bind.TeamDTO.*;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final AuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping
    public ResponseEntity<List<Response>> searchTeams(@Valid @RequestBody SearchRequest request,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        // TODO: jwt를 계속 파싱하는 것도 번거로운데 필터같은걸로 파싱해서 메서드로 전달해줄 수 있나?
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        return ResponseEntity.ok(teamService.searchTeams(request.getSearchTeamField(), request.getSearchValue(), userIdFromClaims, request.isIncludeJoined()));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<Response> getTeamDetails(@PathVariable(name = "teamId") long teamId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.getTeamDetails(teamId));
    }

    @PostMapping
    public ResponseEntity<Response> registerTeam(@Valid @RequestBody RegisterRequest teamDTO,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        Response team = teamService.createTeam(userIdFromClaims, teamDTO);
        return ResponseEntity.created(URIGenerator.createTeam(team.getId())).body(team);
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<Response> updateTeam(@PathVariable(name = "teamId") long teamId,
                                              @Valid @RequestBody UpdateRequest dto,
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
