package com.simpletodolist.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO extends MemberDTO{
    @JsonProperty("token")
    private String token;

    public LoginDTO(Member member) {
        super(member);
    }
}
