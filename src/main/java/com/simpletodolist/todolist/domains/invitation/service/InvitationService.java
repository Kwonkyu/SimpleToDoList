package com.simpletodolist.todolist.domains.invitation.service;

import com.simpletodolist.todolist.domains.invitation.adapter.controller.command.InvitationHandleRequest;
import com.simpletodolist.todolist.domains.invitation.adapter.repository.InvitationRepository;
import com.simpletodolist.todolist.domains.invitation.domain.Invitation;
import com.simpletodolist.todolist.domains.invitation.domain.InvitationEntity;
import com.simpletodolist.todolist.domains.invitation.domain.InvitationStatus;
import com.simpletodolist.todolist.domains.invitation.domain.Invitations;
import com.simpletodolist.todolist.domains.invitation.service.port.InvitationAuthorizationService;
import com.simpletodolist.todolist.domains.invitation.service.port.InvitationCrudService;
import com.simpletodolist.todolist.domains.invitation.service.port.InvitationHandleService;
import com.simpletodolist.todolist.domains.team.adapter.repository.TeamRepository;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.user.adapter.repository.UserRepository;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
class InvitationService implements
	InvitationCrudService, InvitationHandleService, InvitationAuthorizationService {

	private final TeamRepository teamRepository;
	private final UserRepository userRepository;
	private final InvitationRepository invitationRepository;

	@Override
	@Transactional(readOnly = true)
	public Invitations getInvitations(
		Long teamId,
		Long cursor,
		int size
	) {
		TeamEntity team = teamRepository.findTeamById(teamId);
		return new Invitations(
			invitationRepository
				.findAllByTeamAndIdGreaterThanEqual(
					team,
					cursor,
					PageRequest.of(0, size)
				));
	}

	@Override
	@Transactional(readOnly = true)
	public Invitations getInvitations(
		String username,
		Long cursor,
		int size
	) {
		UserEntity user = userRepository.findUserByUsername(username);
		return new Invitations(
			invitationRepository
				.findAllByUserAndIdGreaterThanEqual(
					user,
					cursor,
					PageRequest.of(0, size)
				));
	}

	@Override
	@Transactional(readOnly = true)
	public Invitations getPendingInvitations(
		Long teamId,
		Long cursor,
		int size
	) {
		TeamEntity team = teamRepository.findTeamById(teamId);
		return new Invitations(
			invitationRepository
				.findAllByTeamAndStatusAndIdGreaterThanEqual(
					team,
					InvitationStatus.REQUESTED,
					cursor,
					PageRequest.of(0, size)
				));
	}

	@Override
	@Transactional(readOnly = true)
	public Invitations getPendingInvitations(
		String username,
		Long cursor,
		int size
	) {
		UserEntity user = userRepository.findUserByUsername(username);
		return new Invitations(
			invitationRepository
				.findAllByUserAndStatusAndIdGreaterThanEqual(
					user,
					InvitationStatus.REQUESTED,
					cursor,
					PageRequest.of(0, size)
				));
	}

	@Override
	public Invitation sendInvitationTo(Long teamId, String username) {
		TeamEntity team = teamRepository.findTeamById(teamId);
		UserEntity user = userRepository.findUserByUsername(username);
		return new Invitation(
			invitationRepository.save(
				InvitationEntity
					.builder()
					.user(user)
					.team(team)
					.build()));
	}

	@Override
	public Invitation handleInvitation(
		Long invitationId,
		InvitationHandleRequest request
	) {
		InvitationEntity invitation = invitationRepository.findInvitationById(invitationId);
		switch (request.getStatus()) {
			case ACCEPTED:
				invitation.accept();
				break;

			case REFUSED:
				invitation.refuse();
				break;

			case CANCELLED:
				invitation.cancel();
				break;

			default:
				throw new IllegalStateException("Unable to handle invitation: invalid status");
		}
		return new Invitation(invitation);
	}

	@Override
	public void checkStatusModifyPermission(
		Long invitationId,
		String username,
		InvitationStatus status
	) {
		InvitationEntity invitation = invitationRepository.findInvitationById(invitationId);
		UserEntity invitedUser = invitation.getUser();
		TeamEntity invitingTeam = invitation.getTeam();
		UserEntity teamLeader = invitingTeam.getLeader();
		UserEntity modifier = userRepository.findUserByUsername(username);

		if ((status.isAccepted() || status.isRefused()) && modifier.equals(invitedUser)) {
			return;
		}

		if (status.isCancelled() && (modifier.equals(invitedUser) || modifier.equals(teamLeader))) {
			return;
		}

		throw new AccessDeniedException("Unable to modify invitation: insufficient permission.");
	}
}
