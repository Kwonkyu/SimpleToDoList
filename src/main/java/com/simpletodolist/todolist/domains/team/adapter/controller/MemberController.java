package com.simpletodolist.todolist.domains.team.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.team.domain.Members;
import com.simpletodolist.todolist.domains.team.service.port.MemberService;
import com.simpletodolist.todolist.domains.team.service.port.TeamAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team/{teamId}")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final TeamAuthorizationService authorizationService;

	@GetMapping("/members")
	public ResponseEntity<ApiResponse<Members>> getTeamMembers(
		@AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId
	) {
		authorizationService.checkPublicAccess(teamId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			memberService.getJoinedMembers(teamId)));
	}

	// 추후 invite, request, decline 방식으로 변경.
	@PutMapping("/members/{username}")
	public ResponseEntity<ApiResponse<Members>> inviteNewMember(
	    @AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId,
		@PathVariable(name = "username") String invitingUsername
	) {
		authorizationService.checkLeaderAccess(teamId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(
			memberService.inviteMember(teamId, invitingUsername)));
	}

	@DeleteMapping("/members/{username}")
	public ResponseEntity<ApiResponse<Members>> kickMember(
	    @AuthenticationPrincipal Authentication authentication,
		@PathVariable(name = "teamId") long teamId,
		@PathVariable(name = "username") String targetUsername
	) {
		authorizationService.checkLeaderAccess(teamId, authentication.getName());
		return ResponseEntity.ok(ApiResponse.success(
			memberService.withdrawMember(teamId, targetUsername)));
	}
}
