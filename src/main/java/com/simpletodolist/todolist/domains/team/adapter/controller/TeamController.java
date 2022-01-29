package com.simpletodolist.todolist.domains.team.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamCreateRequest;
import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamSearchRequest;
import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamUpdateRequest;
import com.simpletodolist.todolist.domains.team.domain.SearchedTeams;
import com.simpletodolist.todolist.domains.team.domain.Team;
import com.simpletodolist.todolist.domains.team.service.port.TeamAuthorizationService;
import com.simpletodolist.todolist.domains.team.service.port.TeamCrudService;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

	private final TeamCrudService teamCrudService;
	private final TeamAuthorizationService authorizationService;

	@GetMapping
	public ResponseEntity<ApiResponse<SearchedTeams>> searchTeams(
		@AuthenticationPrincipal Authentication authentication,
		@Valid @RequestBody TeamSearchRequest request
	) {
		return ResponseEntity.ok(ApiResponse.success(
			teamCrudService.searchTeams(request, authentication.getName())));
	}

	@GetMapping("/{teamId}")
	public ResponseEntity<ApiResponse<Team>> getTeamDetails(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId
	) {
		authorizationService.checkPublicAccess(teamId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			teamCrudService.getTeamInformation(teamId)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Team>> registerTeam(
		@AuthenticationPrincipal Authentication authentication,
		@Valid @RequestBody TeamCreateRequest request
	) {
		Team team = teamCrudService.createTeam(request, authentication.getName());
		return ResponseEntity
			.created(URI.create("/api/team/" + team.getId()))
			.body(ApiResponse.success(team));
	}

	@PutMapping("/{teamId}")
	public ResponseEntity<ApiResponse<Team>> updateTeam(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId,
		@Valid @RequestBody TeamUpdateRequest request
	) {
		authorizationService.checkLeaderAccess(teamId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			teamCrudService.updateTeam(teamId, request)));
	}

	@DeleteMapping("/{teamId}")
	public ResponseEntity<Object> removeTeam(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId
	) {
		authorizationService.checkLeaderAccess(teamId, authentication.getName());
		teamCrudService.deleteTeam(teamId);
		return ResponseEntity.noContent()
							 .build();
	}
}
