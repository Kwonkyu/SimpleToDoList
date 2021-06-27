package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team/{teamId}/")
@RequiredArgsConstructor
public class TeamMembersController {

    private final TeamService teamService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping("/members")
    public ResponseEntity<MembersDTO> getTeamMembers(@PathVariable(name = "teamId") long teamId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }


    // TODO: 추후 invite, request, decline 방식으로 변경.
    @PostMapping("/members/{memberUserId}")
    public ResponseEntity<MembersDTO> registerNewMember(@PathVariable(name = "teamId") long teamId,
                                                        @PathVariable(name = "memberUserId") String memberUserId,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.joinMember(teamId, memberUserId));
    }


    @DeleteMapping("/members/{memberUserId}")
    @ResponseStatus(HttpStatus.OK)
    public void withdrawMember(@PathVariable(name = "teamId") long teamId,
                               @PathVariable(name = "memberUserId") String memberUserId,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamLeader(userIdFromClaims, teamId);
        teamService.withdrawMember(teamId, memberUserId);
    }


    @PutMapping("/leader/{memberUserId}")
    public ResponseEntity<TeamDTO> changeTeamLeaderStatus(@PathVariable(name = "teamId") long teamId,
                                                          @PathVariable(name = "memberUserId") String memberUserId,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt));
        teamService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.changeLeader(teamId, memberUserId));
    }
}
