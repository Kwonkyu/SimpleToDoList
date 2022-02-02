package com.simpletodolist.todolist.domains.invitation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Application layer presentation of member invitation.
 */
@Getter
public class Invitation {

	@JsonProperty("id")
	private final long id;

	@JsonProperty("team")
	private final InvitingTeam team;

	@JsonProperty("user")
	private final InvitedUser user;

	@JsonProperty("status")
	private final InvitationStatus status;

	@JsonProperty("invitedDate")
	private final LocalDateTime invited;

	public Invitation(InvitationEntity invitationEntity) {
		this.id = invitationEntity.getId();
		this.team = new InvitingTeam(invitationEntity.getTeam());
		this.user = new InvitedUser(invitationEntity.getUser());
		this.status = invitationEntity.getStatus();
		this.invited = invitationEntity.getInvitedAt();
	}
}
