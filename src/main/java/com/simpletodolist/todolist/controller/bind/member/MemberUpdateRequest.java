package com.simpletodolist.todolist.controller.bind.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MemberUpdateRequest {
    @NotBlank(message = "별명은 공백이 될 수 없습니다.")
    @JsonProperty("alias")
    private String alias;

    @NotBlank(message = "비밀번호는 공백이 될 수 없습니다.")
    @JsonProperty("password")
    private String password;
}
