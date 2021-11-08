package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.controller.bind.member.MemberUpdateRequest;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.member.BasicMemberService;
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
    public ResponseEntity<MemberDTO> memberInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        return ResponseEntity.ok(memberService.getMemberDetails(username));
    }

    @PatchMapping
    public ResponseEntity<MemberDTO> updateMemberInfo(
            @Valid @RequestBody MemberUpdateRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        return ResponseEntity.ok(memberService.updateMember(username, request));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void withdrawMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        memberService.withdrawMember(username);
    }

    @GetMapping("/teams")
    public ResponseEntity<List<TeamDTO>> getTeamsOfMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        return ResponseEntity.ok(memberService.getJoinedTeams(username));
    }

    @PutMapping("/teams/{teamId}")
    public ResponseEntity<TeamDTO> joinTeam(@PathVariable(name = "teamId") long teamId,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        // Member can't join locked team. But team leader can invite member to team(check TeamMembersController).
        TeamDTO teamDTO = memberService.joinTeam(teamId, username);
        return ResponseEntity.ok(teamDTO);
    }

    @DeleteMapping("/teams/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void quitTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                         @PathVariable(name = "teamId") long teamId) {
        String username = jwtTokenUtil.getUsername(jwtTokenUtil.parseBearerJWTSubject(jwt));
        memberService.withdrawTeam(teamId, username);
    }
}
