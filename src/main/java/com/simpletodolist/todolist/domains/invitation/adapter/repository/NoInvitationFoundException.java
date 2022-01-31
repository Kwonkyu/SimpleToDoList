package com.simpletodolist.todolist.domains.invitation.adapter.repository;

import lombok.Getter;

@Getter
public class NoInvitationFoundException extends RuntimeException {

	public NoInvitationFoundException(Long id) {
		super(String.format("Invitation with given id %d not found.", id));
	}
}
