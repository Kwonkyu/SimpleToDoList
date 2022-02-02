package com.simpletodolist.todolist.domains.team.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.team.domain.Team;
import com.simpletodolist.todolist.domains.team.service.port.TeamAuthorizationService;
import com.simpletodolist.todolist.domains.team.service.port.TeamLeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team/{teamId}/")
@RequiredArgsConstructor
public class LeaderController {

	private final TeamLeaderService leaderService;
	private final TeamAuthorizationService authorizationService;

	@PutMapping("/leader/{username}")
	public ResponseEntity<ApiResponse<Team>> changeTeamLeaderStatus(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable(name = "teamId") long teamId,
		@PathVariable(name = "username") String targetUsername
	) {
		authorizationService.checkLeaderPermission(teamId, userDetails.getUsername());
		authorizationService.checkMemberPermission(teamId, targetUsername);
		return ResponseEntity.ok(ApiResponse.success(
			leaderService.changeLeader(teamId, targetUsername)));
	}

}
