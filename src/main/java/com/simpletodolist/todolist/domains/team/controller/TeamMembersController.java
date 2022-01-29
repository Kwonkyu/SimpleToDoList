package com.simpletodolist.todolist.domains.team.controller;

import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.common.util.JwtTokenUtil;
import com.simpletodolist.todolist.common.service.BasicAuthorizationService;
import com.simpletodolist.todolist.domains.team.service.BasicTeamService;
import com.simpletodolist.todolist.common.util.URIGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team/{teamId}/")
@RequiredArgsConstructor
public class TeamMembersController {
    private final BasicTeamService teamService;
    private final BasicAuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<MemberDTO>>> getTeamMembers(@PathVariable(name = "teamId") long teamId,
                                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamMember(teamId, username);
        return ResponseEntity.ok(ApiResponse.success(teamService.getMembers(teamId)));
    }

    // 추후 invite, request, decline 방식으로 변경.
    @PutMapping("/members/{username}")
    public ResponseEntity<ApiResponse<List<MemberDTO>>> registerNewMember(@PathVariable(name = "teamId") long teamId,
                                                                          @PathVariable(name = "username") String targetUsername,
                                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamLeader(teamId, username);
        return ResponseEntity.created(URIGenerator.inviteMemberToteam(targetUsername, teamId))
                .body(ApiResponse.success(teamService.joinMember(teamId, targetUsername)));
    }

    @DeleteMapping("/members/{username}")
    public ResponseEntity<ApiResponse<List<MemberDTO>>> withdrawMember(@PathVariable(name = "teamId") long teamId,
                                                                       @PathVariable(name = "username") String targetUsername,
                                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamLeader(teamId, username);
        return ResponseEntity.ok(ApiResponse.success(teamService.withdrawMember(teamId, targetUsername)));
    }

    @PutMapping("/leader/{username}")
    public ResponseEntity<ApiResponse<TeamDTO>> changeTeamLeaderStatus(@PathVariable(name = "teamId") long teamId,
                                                          @PathVariable(name = "username") String targetUsername,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        authorizationService.authorizeTeamLeader(teamId, username);
        return ResponseEntity.ok(ApiResponse.success(teamService.changeLeader(teamId, targetUsername)));
    }
}