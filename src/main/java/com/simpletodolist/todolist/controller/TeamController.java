package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDTO> getTeamDetails(@PathVariable(name = "teamId") long teamId) {
        return ResponseEntity.ok(teamService.getTeamDetails(teamId));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> registerTeam(@Valid @RequestBody TeamDTO teamDTO) {
        return ResponseEntity.ok(teamService.createTeam(teamDTO));
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeTeam(@PathVariable(name = "teamId") long teamId) {
        teamService.deleteTeam(teamId);
    }


    @GetMapping("/{teamId}/members")
    public ResponseEntity<MembersDTO> getTeamMembers(@PathVariable(name = "teamId") long teamId) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }

    // TODO: 추후 invite, request, decline 방식으로 변경.
    @PostMapping("/{teamId}/members/{memberUserId}")
    public ResponseEntity<MembersDTO> registerNewMember(@PathVariable(name = "teamId") long teamId,
                                                        @PathVariable(name = "memberUserId") String memberUserId) {
        return ResponseEntity.ok(teamService.joinMember(teamId, memberUserId));
    }

    @DeleteMapping("/{teamId}/members/{memberUserId}")
    @ResponseStatus(HttpStatus.OK)
    public void withdrawMember(@PathVariable(name = "teamId") long teamId,
                               @PathVariable(name = "memberUserId") String memberUserId) {
        teamService.withdrawMember(teamId, memberUserId);
    }

    @PutMapping("/{teamId}/members/{memberUserId}")
    public ResponseEntity<MembersDTO> changeTeamLeaderStatus(@PathVariable(name = "teamId") long teamId,
                                                             @PathVariable(name = "memberUserId") String memberUserId) {
        // TODO: team leader change

        return null;
    }
}
