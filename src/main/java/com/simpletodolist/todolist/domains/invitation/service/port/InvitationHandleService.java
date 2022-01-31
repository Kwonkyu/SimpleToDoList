package com.simpletodolist.todolist.domains.invitation.service.port;

import com.simpletodolist.todolist.domains.invitation.adapter.controller.command.InvitationHandleRequest;
import com.simpletodolist.todolist.domains.invitation.domain.Invitation;

public interface InvitationHandleService {

	/**
	 * Accept, refuse or cancel given invitation.
	 *
	 * @param invitationId the id of invitation.
	 * @param request invitation handle request.
	 * @return the information of handled invitation.
	 */
	Invitation handleInvitation(
		Long invitationId,
		InvitationHandleRequest request
	);

}
