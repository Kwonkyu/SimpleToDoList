package com.simpletodolist.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDTO extends MemberDTO{
    @JsonProperty("token")
    private String token;

    public LoginDTO(Member member, String token) {
        super(member);
        this.token = token;
    }
}
