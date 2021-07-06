package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.*;
import com.simpletodolist.todolist.exception.member.InvalidTeamWithdrawException;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TeamService teamService;
    private final JwtTokenUtil jwtTokenUtil;
    private final MessageSource messageSource;


    /**
     * Get information of user based on given user id.
     * @return MemberDTO object filled with user information.
     */
    @GetMapping
    public ResponseEntity<MemberDTO> memberInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        return ResponseEntity.ok(memberService.getMemberDetails(userIdFromClaims));
    }



    @PatchMapping
    public ResponseEntity<MemberDTO> updateMemberInfo(@Valid @RequestBody MemberInformationUpdateRequestDTO requestDTO,
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


    /**
     * Get teams member joined.
     * @param jwt JWT
     * @return TeamsDTO object filled with member's teams.
     */
    @GetMapping("/teams")
    public ResponseEntity<TeamsDTO> getTeamsOfMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        jwtTokenUtil.validateRequestedUserIdWithJwt(userIdFromClaims, jwt, messageSource.getMessage("unauthorized.member", null, Locale.KOREAN));
        return ResponseEntity.ok(memberService.getTeamsOfMember(userIdFromClaims));
    }


    @PostMapping("/teams")
    public ResponseEntity<MembersDTO> joinTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                                            @Valid @RequestBody TeamIdRequestDTO dto) {
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        if(teamService.validateTeamLocked(dto.getTeamId())) {
            teamService.authorizeTeamLeader(userIdFromClaims, dto.getTeamId());
        }

        return ResponseEntity.ok(teamService.joinMember(dto.getTeamId(), userIdFromClaims));
    }

    @DeleteMapping("/teams/{teamId}")
    public ResponseEntity<MembersDTO> quitTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwt,
                                               @PathVariable(name = "teamId") long teamId) {
        String userIdFromClaims = jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateBearerJWT(jwt));
        // TODO: note. it causes delete not flushed. why?
//        TeamsDTO teamsOfMember = memberService.getTeamsOfMember(userIdFromClaims);
//        if(teamsOfMember.getTeams().stream().noneMatch(teamDTO -> teamDTO.getId() == dto.getTeamId())) {
//            throw new InvalidTeamWithdrawException();
//        }

        return ResponseEntity.ok(teamService.withdrawMember(teamId, userIdFromClaims));
    }

}
