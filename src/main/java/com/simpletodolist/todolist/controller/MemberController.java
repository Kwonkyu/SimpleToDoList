package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.exception.team.LockedTeamException;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.AuthorizationService;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.util.URIGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

import static com.simpletodolist.todolist.domain.bind.MemberDTO.Response;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TeamService teamService;
    private final AuthorizationService authorizationService;
    private final JwtTokenUtil jwtTokenUtil;
    private final MessageSource messageSource;


    @GetMapping
    public ResponseEntity<Response> memberInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        return ResponseEntity.ok(memberService.getMemberDetails(userIdFromClaims));
    }



    @PatchMapping
    public ResponseEntity<Response> updateMemberInfo(@Valid @RequestBody MemberDTO.UpdateRequest requestDTO,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        jwtTokenUtil.validateRequestedUserIdWithJwt(userIdFromClaims, jwt, messageSource.getMessage("unauthorized.member", null, Locale.KOREAN));
        return ResponseEntity.ok(memberService.updateMember(userIdFromClaims, requestDTO.getField(), requestDTO.getValue()));
    }


    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void withdrawMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        jwtTokenUtil.validateRequestedUserIdWithJwt(userIdFromClaims, jwt, messageSource.getMessage("unauthorized.member", null, Locale.KOREAN));
        memberService.withdrawMember(userIdFromClaims);
    }


    @GetMapping("/teams")
    public ResponseEntity<List<TeamDTO.Response>> getTeamsOfMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        jwtTokenUtil.validateRequestedUserIdWithJwt(userIdFromClaims, jwt, messageSource.getMessage("unauthorized.member", null, Locale.KOREAN));
        return ResponseEntity.ok(memberService.getTeamsOfMember(userIdFromClaims));
    }


    @GetMapping("/teams/{teamId}")
    public ResponseEntity<TeamDTO.Response> getTeamDetails(@PathVariable(name = "teamId") long teamId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        authorizationService.authorizeTeamMember(userIdFromClaims, teamId);
        return ResponseEntity.ok(teamService.getTeamDetails(teamId));
    }


    @PutMapping("/teams/{teamId}")
    public ResponseEntity<List<MemberDTO.Response>> joinTeam(@PathVariable(name = "teamId") long teamId,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        if (teamService.isTeamLocked(teamId)) throw new LockedTeamException();
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        // Member can't join locked team. But team leader can invite member to team(check TeamMembersController).
        List<MemberDTO.Response> membersDTO = teamService.joinMember(teamId, userIdFromClaims);
        return ResponseEntity.created(URIGenerator.joinTeam(teamId)).body(membersDTO);
    }


    @DeleteMapping("/teams/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void quitTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                         @PathVariable(name = "teamId") long teamId) {
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        // TODO: note. it causes delete not flushed. why?
//        TeamsDTO teamsOfMember = memberService.getTeamsOfMember(userIdFromClaims);
//        if(teamsOfMember.getTeams().stream().noneMatch(teamDTO -> teamDTO.getId() == dto.getTeamId())) {
//            throw new InvalidTeamWithdrawException();
//        }
        teamService.withdrawMember(teamId, userIdFromClaims);
    }

}
