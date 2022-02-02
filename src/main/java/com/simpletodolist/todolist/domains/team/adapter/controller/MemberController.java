package com.simpletodolist.todolist.domains.team.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.team.domain.Members;
import com.simpletodolist.todolist.domains.team.service.port.MemberService;
import com.simpletodolist.todolist.domains.team.service.port.TeamAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final TeamAuthorizationService teamAuthService;

	@GetMapping
	public ResponseEntity<ApiResponse<Members>> getTeamMembers(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam("team") long teamId
	) {
		teamAuthService.checkPublicAccess(teamId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			memberService.getJoinedMembers(teamId)));
	}

	@DeleteMapping
	public ResponseEntity<ApiResponse<Members>> kickMember(
	    @AuthenticationPrincipal UserDetails userDetails,
		@RequestParam("username") String targetUsername,
		@RequestParam("team") long teamId
	) {
		teamAuthService.checkLeaderPermission(teamId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			memberService.withdrawMember(teamId, targetUsername)));
	}
}
