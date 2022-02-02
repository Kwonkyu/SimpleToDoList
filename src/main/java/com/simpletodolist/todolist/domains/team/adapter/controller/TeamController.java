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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

	private final TeamCrudService teamCrudService;
	private final TeamAuthorizationService authorizationService;

	@GetMapping
	public ResponseEntity<ApiResponse<SearchedTeams>> searchTeams(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam("field") String searchField,
		@RequestParam("value") String searchValue,
		@RequestParam("joined") boolean includeJoined,
		@RequestParam(name = "page", required = false, defaultValue = "0") int page,
		@RequestParam(name = "size", required = false, defaultValue = "10") int size
	) {
		TeamSearchRequest request = new TeamSearchRequest();
		request.setSearchField(TeamSearchRequest.SearchField.valueOf(searchField));
		request.setSearchValue(searchValue);
		request.setIncludeJoined(includeJoined);
		request.setPage(page);
		request.setSize(size);
		return ResponseEntity.ok(ApiResponse.success(
			teamCrudService.searchTeams(request, userDetails.getUsername())));
	}

	@GetMapping("/{teamId}")
	public ResponseEntity<ApiResponse<Team>> getTeamDetails(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable(name = "teamId") long teamId
	) {
		authorizationService.checkPublicAccess(teamId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			teamCrudService.getTeamInformation(teamId)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Team>> registerTeam(
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody TeamCreateRequest request
	) {
		Team team = teamCrudService.createTeam(request, userDetails.getUsername());
		return ResponseEntity
			.created(URI.create("/api/team/" + team.getId()))
			.body(ApiResponse.success(team));
	}

	@PutMapping("/{teamId}")
	public ResponseEntity<ApiResponse<Team>> updateTeam(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable(name = "teamId") long teamId,
		@Valid @RequestBody TeamUpdateRequest request
	) {
		authorizationService.checkLeaderPermission(teamId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			teamCrudService.updateTeam(teamId, request)));
	}

	@DeleteMapping("/{teamId}")
	public ResponseEntity<Object> removeTeam(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable(name = "teamId") long teamId
	) {
		authorizationService.checkLeaderPermission(teamId, userDetails.getUsername());
		teamCrudService.deleteTeam(teamId);
		return ResponseEntity.noContent()
							 .build();
	}
}
