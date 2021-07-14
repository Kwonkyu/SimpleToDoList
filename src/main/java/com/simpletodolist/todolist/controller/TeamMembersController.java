package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.AuthorizationService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.util.URIGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.simpletodolist.todolist.domain.bind.MemberDTO.*;

@RestController
@RequestMapping("/api/team/{teamId}/")
@RequiredArgsConstructor
public class TeamMembersController {

    private final TeamService teamService;
    private final AuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping("/members")
    public ResponseEntity<List<BasicWithTodoLists>> getTeamMembers(@PathVariable(name = "teamId") long teamId,
                                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }


    @GetMapping("/members/{userId}")
    public ResponseEntity<BasicWithTodoLists> memberInfo(@PathVariable(name = "teamId") long teamId,
                                                         @PathVariable(name = "userId") String userId,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.getTeamMemberInformation(teamId, userId));
    }

    // TODO: 추후 invite, request, decline 방식으로 변경.
    @PutMapping("/members/{userId}")
    public ResponseEntity<List<Basic>> registerNewMember(@PathVariable(name = "teamId") long teamId,
                                                        @PathVariable(name = "userId") String userId,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.created(URIGenerator.inviteMemberToteam(userId, teamId)).body(teamService.joinMember(teamId, userId));
    }


    @DeleteMapping("/members/{userId}")
    public ResponseEntity<List<Basic>> withdrawMember(@PathVariable(name = "teamId") long teamId,
                                                     @PathVariable(name = "userId") String userId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.withdrawMember(teamId, userId));
    }


    @PutMapping("/leader/{memberUserId}")
    public ResponseEntity<TeamDTO.Basic> changeTeamLeaderStatus(@PathVariable(name = "teamId") long teamId,
                                                          @PathVariable(name = "memberUserId") String memberUserId,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.changeLeader(teamId, memberUserId));
    }
}
