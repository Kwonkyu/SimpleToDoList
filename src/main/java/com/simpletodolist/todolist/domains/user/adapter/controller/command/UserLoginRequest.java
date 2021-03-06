package com.simpletodolist.todolist.domains.user.adapter.controller.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserLoginRequest {

	@NotBlank(message = "사용자 ID는 비워둘 수 없습니다")
	@Length(max = 32, message = "아이디는 32 글자를 초과할 수 없습니다.")
	@JsonProperty("username")
	private String username;

	@NotBlank(message = "사용자 비밀번호는 비워둘 수 없습니다.")
	@Length(max = 64, message = "비밀번호는 64 글자를 초과할 수 없습니다.")
	@JsonProperty("password")
	private String password;
}
