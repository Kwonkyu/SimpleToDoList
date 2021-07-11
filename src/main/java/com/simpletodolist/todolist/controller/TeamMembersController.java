package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.MemberDTO;
import com.simpletodolist.todolist.controller.bind.MembersDTO;
import com.simpletodolist.todolist.controller.bind.TeamDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.AuthorizationService;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.util.URIGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team/{teamId}/")
@RequiredArgsConstructor
public class TeamMembersController {

    private final MemberService memberService;
    private final TeamService teamService;
    private final AuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping("/members")
    public ResponseEntity<MembersDTO> getTeamMembers(@PathVariable(name = "teamId") long teamId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }


    @GetMapping("/members/{userId}")
    public ResponseEntity<MemberDTO> memberInfo(@PathVariable(name = "teamId") long teamId,
            @PathVariable(name = "userId") String userId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(memberService.getMemberDetails(userId));
    }

    // TODO: 추후 invite, request, decline 방식으로 변경.
    @PutMapping("/members/{userId}")
    public ResponseEntity<MembersDTO> registerNewMember(@PathVariable(name = "teamId") long teamId,
                                                        @PathVariable(name = "userId") String userId,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.created(URIGenerator.inviteMemberToteam(userId, teamId)).body(teamService.joinMember(teamId, userId));
    }


    @DeleteMapping("/members/{userId}")
    public ResponseEntity<MembersDTO> withdrawMember(@PathVariable(name = "teamId") long teamId,
                                                     @PathVariable(name = "userId") String userId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.withdrawMember(teamId, userId));
    }


    @PutMapping("/leader/{memberUserId}")
    public ResponseEntity<TeamDTO> changeTeamLeaderStatus(@PathVariable(name = "teamId") long teamId,
                                                          @PathVariable(name = "memberUserId") String memberUserId,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.changeLeader(teamId, memberUserId));
    }
}
