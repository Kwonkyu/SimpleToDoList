package com.simpletodolist.todolist.controller.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MemberDTO {

    @JsonProperty("id")
    private long id;

    @NotBlank(message = "사용자 ID는 비워둘 수 없습니다", groups = {RegisterValidationGroup.class, LoginValidationGroup.class})
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

    @JsonProperty("teams")
    private List<TeamDTO> teams = new ArrayList<>();

    @JsonProperty("locked")
    private boolean locked;

    public MemberDTO(Member member){
        id = member.getId();
        userId = member.getUserId();
        username = member.getUsername();
        password = "ENCRYPTED";
        teams = member.getTeamDTOList();
        locked = member.isLocked();
    }

    public MemberDTO(String userId, String username, String password) {
        this(userId, password);
        this.username = username;
    }

    public MemberDTO(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }


    public interface RegisterValidationGroup {}
    public interface LoginValidationGroup {}
}
