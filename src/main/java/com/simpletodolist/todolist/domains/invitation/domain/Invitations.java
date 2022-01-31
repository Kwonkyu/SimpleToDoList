package com.simpletodolist.todolist.domains.invitation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class Invitations {

	@JsonProperty("page")
	private final int page;

	@JsonProperty("pages")
	private final int pages;

	@JsonProperty("size")
	private final int size;

	@JsonProperty("invitations")
	private final List<Invitation> invitationList;

	public Invitations(Page<InvitationEntity> page) {
		this.page = page.getNumber();
		this.pages = page.getTotalPages();
		this.size = page.getSize();
		this.invitationList = page
			.stream()
			.map(Invitation::new)
			.collect(Collectors.toList());
	}
}
