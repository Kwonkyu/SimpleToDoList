package com.simpletodolist.todolist.domains.team.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Application layer presentation of member list.
 */
@Getter
public class Members {

	@JsonProperty("members")
	private final List<Member> memberList;

	public Members(List<UserEntity> userEntities) {
		this.memberList = userEntities
			.stream()
			.map(Member::new)
			.collect(Collectors.toList());
	}

}
