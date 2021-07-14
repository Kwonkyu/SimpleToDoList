package com.simpletodolist.todolist.domain.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TeamDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("id")
        public long id;

        @JsonProperty("teamLeaderUserId")
        public String teamLeaderUserId;

        @JsonProperty("teamLeaderUsername")
        public String teamLeaderUsername;

        @JsonProperty("teamName")
        public String teamName;

        @JsonProperty("todoLists")
        public List<TodoListDTO.Response> todoLists;

        @JsonProperty("locked")
        public boolean locked;

        public Response(Team team) {
            id = team.getId();
            teamLeaderUserId = team.getLeader().getUserId();
            teamLeaderUsername = team.getLeader().getUsername();
            teamName = team.getTeamName();
            todoLists = team.getTodoLists().stream().map(TodoListDTO.Response::new).collect(Collectors.toList());
            locked = team.isLocked();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor // TODO: why these needed?
    public static class RegisterRequest {
        @NotBlank(message = "팀 이름은 비워둘 수 없습니다.")
        @Length(max = 64, message = "팀 이름은 64 글자를 초과할 수 없습니다.")
        @JsonProperty("teamName")
        private String teamName;
    }

    @Getter
    @Setter
    @Builder
    public static class UpdateRequest {
        @NotNull(message = "허용되지 않은 수정할 필드입니다.")
        private UpdatableTeamInformation field;

        @NotNull(message = "수정할 값은 비워둘 수 없습니다.")
        private Object value;

        public enum UpdatableTeamInformation {
            NAME, LOCKED
        }
    }

    @Getter
    @Setter
    @Builder
    public static class SearchRequest {
        @NotNull(message = "탐색 항목은 비워둘 수 없습니다.")
        @JsonProperty("field")
        private SearchTeamField searchTeamField;

        @NotBlank(message = "검색 값은 비워둘 수 없습니다.")
        @JsonProperty("value")
        private String searchValue;

        @JsonProperty("joined")
        private boolean includeJoined;

        public enum SearchTeamField {
            NAME, LEADER
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Basic {
        @JsonProperty("id")
        private long id;

        @JsonProperty("teamLeaderUserId")
        private String teamLeaderUserId;

        @JsonProperty("teamLeaderUsername")
        private String teamLeaderUsername;

        @JsonProperty("teamName")
        private String teamName;

        @JsonProperty("locked")
        private boolean locked;

        public Basic(Team team) {
            this.id = team.getId();
            Member leader = team.getLeader();
            this.teamLeaderUserId = leader.getUserId();
            this.teamLeaderUsername = leader.getUsername();
            this.teamName = team.getTeamName();
            this.locked = team.isLocked();
        }
    }

    @Getter
    @Setter
    public static class BasicWithJoined extends Basic {
        @JsonProperty("joined")
        private boolean joined;

        public BasicWithJoined(Team team, boolean joined) {
            super(team);
            this.joined = joined;
        }
    }
}
