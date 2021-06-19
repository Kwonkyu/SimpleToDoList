package com.simpletodolist.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class TeamDTO {

    @JsonProperty("id")
    private long id;

    @NotBlank
    @JsonProperty("leaderUserId")
    private String teamLeaderUserId;

    @JsonProperty("leaderUsername")
    private String teamLeaderUsername;

    @NotBlank
    @Length(max = 64)
    @JsonProperty("teamName")
    private String teamName;

    public TeamDTO(Team team){
        this.id = team.getId();
        this.teamLeaderUserId = team.getLeader().getUserId();
        this.teamLeaderUsername = team.getLeader().getUsername();
        this.teamName = team.getTeamName();
    }
}
