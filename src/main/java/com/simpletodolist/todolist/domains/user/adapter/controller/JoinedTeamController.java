package com.simpletodolist.todolist.domains.user.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.user.domain.JoinedTeam;
import com.simpletodolist.todolist.domains.user.domain.JoinedTeams;
import com.simpletodolist.todolist.domains.user.service.port.JoinedTeamManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class JoinedTeamController {

	private final JoinedTeamManageService joinedTeamManageService;

	@GetMapping("/teams")
	public ResponseEntity<ApiResponse<JoinedTeams>> getTeamsOfMember(
		@AuthenticationPrincipal Authentication authentication
	) {
		return ResponseEntity.ok(ApiResponse.success(
			joinedTeamManageService.getJoinedTeams(authentication.getName())));
	}

	@PutMapping("/teams/{teamId}")
	public ResponseEntity<ApiResponse<JoinedTeam>> joinTeam(
		@PathVariable(name = "teamId") long teamId,
		@AuthenticationPrincipal Authentication authentication
	) {
		// Member can't join locked team. But team leader can invite member to team(check TeamMembersController).
		return ResponseEntity.ok(ApiResponse.success(
			joinedTeamManageService.joinTeam(teamId, authentication.getName())
		));
	}

	@DeleteMapping("/teams/{teamId}")
	@ResponseStatus(HttpStatus.OK)
	public void quitTeam(
		@PathVariable(name = "teamId") long teamId,
		@AuthenticationPrincipal Authentication authentication
	) {
		joinedTeamManageService.withdrawTeam(teamId, authentication.getName());
	}
}
