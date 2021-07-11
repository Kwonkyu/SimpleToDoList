package com.simpletodolist.todolist.controller.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import com.simpletodolist.todolist.domain.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TeamDTO {

    @JsonProperty("id")
    private long id;

    @JsonProperty("teamLeaderUserId")
    private String teamLeaderUserId;

    @JsonProperty("teamLeaderUsername")
    private String teamLeaderUsername;

    @NotBlank
    @Length(max = 64)
    @JsonProperty("teamName")
    private String teamName;

    @JsonProperty("members")
    private List<String> members = new ArrayList<>();

    @JsonProperty("locked")
    private boolean locked;

    public TeamDTO(Team team){
        this.id = team.getId();
        this.teamLeaderUserId = team.getLeader().getUserId();
        this.teamLeaderUsername = team.getLeader().getUsername();
        this.teamName = team.getTeamName();
        this.members = team.getMembers().stream().map(MemberTeamAssociation::getMember).map(Member::getUserId).collect(Collectors.toList());
        this.locked = team.isLocked();
    }
}
