package com.simpletodolist.todolist.domains.invitation.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.invitation.adapter.controller.command.InvitationCreateRequest;
import com.simpletodolist.todolist.domains.invitation.adapter.controller.command.InvitationHandleRequest;
import com.simpletodolist.todolist.domains.invitation.domain.Invitation;
import com.simpletodolist.todolist.domains.invitation.domain.Invitations;
import com.simpletodolist.todolist.domains.invitation.service.port.InvitationAuthorizationService;
import com.simpletodolist.todolist.domains.invitation.service.port.InvitationCrudService;
import com.simpletodolist.todolist.domains.invitation.service.port.InvitationHandleService;
import com.simpletodolist.todolist.domains.team.service.port.TeamAuthorizationService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invitations")
public class MemberInvitationController {

	private final InvitationCrudService crudService;
	private final InvitationHandleService handleService;
	private final TeamAuthorizationService teamAuthService;
	private final InvitationAuthorizationService invitationAuthService;

	@GetMapping("/user")
	public ResponseEntity<ApiResponse<Invitations>> getInvitationsOfUser(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(name = "cursor", required = false, defaultValue = "0") long cursor,
		@RequestParam(name = "size", required = false, defaultValue = "10") int size,
		@RequestParam(name = "pending", required = false, defaultValue = "false") boolean pending
	) {
		return ResponseEntity.ok(ApiResponse.success(
			pending ?
				crudService.getPendingInvitations(userDetails.getUsername(), cursor, size) :
				crudService.getInvitations(userDetails.getUsername(), cursor, size)));
	}

	@GetMapping("/team/{teamId}")
	public ResponseEntity<ApiResponse<Invitations>> getInvitationsOfTeam(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable("teamId") long teamId,
		@RequestParam(name = "cursor", required = false, defaultValue = "0") long cursor,
		@RequestParam(name = "size", required = false, defaultValue = "10") int size,
		@RequestParam(name = "pending", required = false, defaultValue = "false") boolean pending
	) {
		teamAuthService.checkLeaderPermission(teamId, userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			pending ?
				crudService.getPendingInvitations(teamId, cursor, size) :
				crudService.getInvitations(teamId, cursor, size)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Invitation>> sendInvitation(
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody InvitationCreateRequest request
	) {
		teamAuthService.checkLeaderPermission(
			request.getTeamId(), userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success(
			crudService.sendInvitationTo(request.getTeamId(), request.getUsername())));
	}

	@PutMapping("/{invitationId}")
	public ResponseEntity<ApiResponse<Invitation>> handleInvitation(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable("invitationId") long invitationId,
		@Valid @RequestBody InvitationHandleRequest request
	) {
		invitationAuthService.checkStatusModifyPermission(
			invitationId, userDetails.getUsername(), request.getStatus());
		return ResponseEntity.ok(ApiResponse.success(
			handleService.handleInvitation(invitationId, request)));
	}

}
