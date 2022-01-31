package com.simpletodolist.todolist.domains.invitation.service.port;

import com.simpletodolist.todolist.domains.invitation.domain.Invitation;
import com.simpletodolist.todolist.domains.invitation.domain.Invitations;

public interface InvitationCrudService {

	/**
	 * Get sent invitations of team by page.
	 *
	 * @param teamId the id of team.
	 * @param cursor the id of cursor invitation.
	 * @param size the paging size.
	 * @return the list of sent invitations.
	 */
	Invitations getInvitations(
		Long teamId,
		Long cursor,
		int size
	);

	/**
	 * Get received invitations of user.
	 *
	 * @param username the username of user.
	 * @param cursor the id of cursor invitation.
	 * @param size the paging size.
	 * @return the list of received invitations.
	 */
	Invitations getInvitations(
		String username,
		Long cursor,
		int size
	);

	/**
	 * Get not handled(not accepted nor refused) invitations of team.
	 *
	 * @param teamId the id of team.
	 * @param cursor the id of cursor invitation.
	 * @param size the paging size.
	 * @return the list of not handled invitations.
	 */
	Invitations getPendingInvitations(
		Long teamId,
		Long cursor,
		int size
	);

	/**
	 * Get not handled(not accepted nor refused) invitations of user.
	 *
	 * @param username the username of user.
	 * @param cursor the id of cursor invitation.
	 * @param size the paging size.
	 * @return the list of not handled invitations.
	 */
	Invitations getPendingInvitations(
		String username,
		Long cursor,
		int size
	);

	/**
	 * Send invitation from team to user.
	 *
	 * @param teamId the id of team.
	 * @param username the username of user.
	 * @return the sent invitation's information.
	 */
	Invitation sendInvitationTo(Long teamId, String username);
}
