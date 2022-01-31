package com.simpletodolist.todolist.domains.invitation.service.port;

import com.simpletodolist.todolist.domains.invitation.domain.InvitationStatus;

public interface InvitationAuthorizationService {

	/**
	 * Check whether user can update invitation.
	 *
	 * @param invitationId the id of invitation.
	 * @param username     the username of user.
	 * @param status       the updating status of invitation.
	 */
	void checkStatusModifyPermission(Long invitationId, String username, InvitationStatus status);

}
