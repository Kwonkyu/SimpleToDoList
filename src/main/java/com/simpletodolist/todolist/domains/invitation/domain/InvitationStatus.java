package com.simpletodolist.todolist.domains.invitation.domain;

public enum InvitationStatus {
	REQUESTED, CANCELLED, ACCEPTED, REFUSED;

	public boolean isRequested() {
		return this.equals(REQUESTED);
	}

	public boolean isAccepted() {
		return this.equals(ACCEPTED);
	}

	public boolean isRefused() {
		return this.equals(REFUSED);
	}

	public boolean isCancelled() {
		return this.equals(CANCELLED);
	}
}
