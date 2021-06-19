package com.simpletodolist.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class MemberDTO {

    @JsonProperty("id")
    private long id;

    @NotBlank
    @Length(max=32)
    @JsonProperty("userId")
    private String userId;

    @NotBlank
    @Length(max=32)
    @JsonProperty("username")
    private String username;

    @NotBlank
    @Length(max=64)
    @JsonProperty("password")
    private String password;

    public MemberDTO(Member member){
        this.id = member.getId();
        this.userId = member.getUserId();
        this.username = member.getUsername();
        this.password = "ENCRYPTED";
    }


}
