package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.exception.general.AuthorizationFailedException;
import com.simpletodolist.todolist.security.JwtTokenUtil;
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
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final JwtTokenUtil jwtTokenUtil;
    private final MessageSource messageSource;


    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDTO> getTeamDetails(@PathVariable(name = "teamId") long teamId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        if(!teamService.authorizeTeamMember(jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt)), teamId)) {
            throw new AuthorizationFailedException(
                    AuthorizationFailedException.DEFAULT_ERROR,
                    messageSource.getMessage("unauthorized.team.get", null, Locale.KOREAN));
        }
        return ResponseEntity.ok(teamService.getTeamDetails(teamId));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> registerTeam(@Valid @RequestBody TeamDTO teamDTO,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        teamDTO.setTeamLeaderUserId(jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt)));
        return ResponseEntity.ok(teamService.createTeam(teamDTO));
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeTeam(@PathVariable(name = "teamId") long teamId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        if(!teamService.authorizeTeamLeader(jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt)), teamId)) {
            throw new AuthorizationFailedException(
                    AuthorizationFailedException.DEFAULT_ERROR,
                    messageSource.getMessage("unauthorized.team.delete", null, Locale.KOREAN));
        }
        teamService.deleteTeam(teamId);
    }


    @GetMapping("/{teamId}/members")
    public ResponseEntity<MembersDTO> getTeamMembers(@PathVariable(name = "teamId") long teamId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        if(!teamService.authorizeTeamMember(jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt)), teamId)) {
            throw new AuthorizationFailedException(
                    AuthorizationFailedException.DEFAULT_ERROR,
                    messageSource.getMessage("unauthorized.team.members.get", null, Locale.KOREAN));
        }
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }

    // TODO: 추후 invite, request, decline 방식으로 변경.
    @PostMapping("/{teamId}/members/{memberUserId}")
    public ResponseEntity<MembersDTO> registerNewMember(@PathVariable(name = "teamId") long teamId,
                                                        @PathVariable(name = "memberUserId") String memberUserId,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        if(!teamService.authorizeTeamLeader(jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt)), teamId)){
            throw new AuthorizationFailedException(
                    AuthorizationFailedException.DEFAULT_ERROR,
                    messageSource.getMessage("unauthorized.team.members.join", null, Locale.KOREAN));
        }
        return ResponseEntity.ok(teamService.joinMember(teamId, memberUserId));
    }

    @DeleteMapping("/{teamId}/members/{memberUserId}")
    @ResponseStatus(HttpStatus.OK)
    public void withdrawMember(@PathVariable(name = "teamId") long teamId,
                               @PathVariable(name = "memberUserId") String memberUserId,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        if(!teamService.authorizeTeamLeader(jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt)), teamId)) {
            throw new AuthorizationFailedException(
                    AuthorizationFailedException.DEFAULT_ERROR,
                    messageSource.getMessage("unauthorized.team.members.withdraw", null, Locale.KOREAN));
        }
        teamService.withdrawMember(teamId, memberUserId);
    }

    @PutMapping("/{teamId}/leader/{memberUserId}")
    public ResponseEntity<MembersDTO> changeTeamLeaderStatus(@PathVariable(name = "teamId") long teamId,
                                                             @PathVariable(name = "memberUserId") String memberUserId,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        if(!teamService.authorizeTeamLeader(jwtTokenUtil.getUserIdFromClaims(jwtTokenUtil.validateJwtToken(jwt)), teamId)) {
            throw new AuthorizationFailedException(
                    AuthorizationFailedException.DEFAULT_ERROR,
                    messageSource.getMessage("unauthorized.team.members.leader", null, Locale.KOREAN));
        }
        // TODO: team leader change feature

        return null;
    }
}
