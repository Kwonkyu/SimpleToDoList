package com.simpletodolist.todolist.domains.team.bind.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TeamInformationRequest {
    @NotBlank(message = "팀 이름은 비워둘 수 없습니다.")
    private String teamName;

    private boolean locked;
}
