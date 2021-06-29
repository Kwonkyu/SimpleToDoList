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
public class MemberDTO extends TokenDTO{

    @JsonProperty("id")
    private long id;

    @NotBlank(groups = {RegisterValidationGroup.class, LoginValidationGroup.class})
    @Length(max=32, groups = {RegisterValidationGroup.class, LoginValidationGroup.class})
    @JsonProperty("userId")
    private String userId;

    @NotBlank(groups = {RegisterValidationGroup.class})
    @Length(max=32, groups = {RegisterValidationGroup.class})
    @JsonProperty("username")
    private String username;

    @NotBlank(groups = {RegisterValidationGroup.class, LoginValidationGroup.class})
    @Length(max=64, groups = {RegisterValidationGroup.class, LoginValidationGroup.class})
    @JsonProperty("password")
    private String password;

    public MemberDTO(Member member){
        this.id = member.getId();
        this.userId = member.getUserId();
        this.username = member.getUsername();
        this.password = "ENCRYPTED";
    }


    public interface RegisterValidationGroup {}
    public interface LoginValidationGroup {}
}
