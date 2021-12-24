package com.simpletodolist.todolist.domains.member.controller;

import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.member.bind.request.MemberUpdateRequest;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.common.util.JwtTokenUtil;
import com.simpletodolist.todolist.domains.member.service.BasicMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final BasicMemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;


    @GetMapping
    public ResponseEntity<ApiResponse<MemberDTO>> memberInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        return ResponseEntity.ok(ApiResponse.success(memberService.getMemberDetails(username)));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<MemberDTO>> updateMemberInfo(
            @Valid @RequestBody MemberUpdateRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        return ResponseEntity.ok(ApiResponse.success(memberService.updateMember(username, request)));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void withdrawMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        memberService.withdrawMember(username);
    }

    @GetMapping("/teams")
    public ResponseEntity<ApiResponse<List<TeamDTO>>> getTeamsOfMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        return ResponseEntity.ok(ApiResponse.success(memberService.getJoinedTeams(username)));
    }

    @PutMapping("/teams/{teamId}")
    public ResponseEntity<ApiResponse<TeamDTO>> joinTeam(@PathVariable(name = "teamId") long teamId,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        // Member can't join locked team. But team leader can invite member to team(check TeamMembersController).
        TeamDTO teamDTO = memberService.joinTeam(teamId, username);
        return ResponseEntity.ok(ApiResponse.success(teamDTO));
    }

    @DeleteMapping("/teams/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void quitTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                         @PathVariable(name = "teamId") long teamId) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        memberService.withdrawTeam(teamId, username);
    }
}
