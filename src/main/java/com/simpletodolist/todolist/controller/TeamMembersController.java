package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.MemberJoinTeamDTO;
import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.AuthorizationService;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.util.URIGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        return ResponseEntity.ok(memberService.getMemberDetails(userIdFromClaims));
    }

    // TODO: 추후 invite, request, decline 방식으로 변경.
    @PostMapping("/members")
    public ResponseEntity<MembersDTO> registerNewMember(@PathVariable(name = "teamId") long teamId,
                                                        @Valid @RequestBody MemberJoinTeamDTO dto,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.created(URIGenerator.inviteMemberToteam(dto.userId, teamId)).body(teamService.joinMember(teamId, dto.userId));
    }


    @DeleteMapping("/members/{memberUserId}")
    public ResponseEntity<MembersDTO> withdrawMember(@PathVariable(name = "teamId") long teamId,
                               @PathVariable(name = "memberUserId") String memberUserId,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamLeader(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.withdrawMember(teamId, memberUserId));
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
