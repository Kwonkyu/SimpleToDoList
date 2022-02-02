package com.simpletodolist.todolist.domains.invitation.adapter.controller.command;

import com.simpletodolist.todolist.domains.invitation.domain.InvitationStatus;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationHandleRequest {

	@NotNull InvitationStatus status;

}
