package com.simpletodolist.todolist.domains.invitation.adapter.controller.command;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationCreateRequest {

	@NotNull
	private long teamId;

	@NotBlank
	private String username;

}
