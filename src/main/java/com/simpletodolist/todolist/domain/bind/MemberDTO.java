package com.simpletodolist.todolist.domain.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.TodoList;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MemberDTO {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("id")
        long id;

        @JsonProperty("userId")
        String userId;

        @JsonProperty("username")
        String username;

        @JsonProperty("password")
        String password;

        @JsonProperty("teams")
        List<TeamDTO.Response> teams;

        @JsonProperty("locked")
        boolean locked;

        public Response(Member member) {
            id = member.getId();
            userId = member.getUserId();
            username = member.getUsername();
            password = "ENCRYPTED";
            teams = member.getTeamsDTO();
            locked = member.isLocked();
        }
    }

    @Builder
    @Getter
    @Setter
    public static class LoginRequest {
        @NotBlank(message = "사용자 ID는 비워둘 수 없습니다")
        @Length(max = 32, message = "아이디는 32 글자를 초과할 수 없습니다.")
        @JsonProperty("userId")
        String userId;

        @NotBlank(message = "사용자 비밀번호는 비워둘 수 없습니다.")
        @Length(max = 64, message = "비밀번호는 64 글자를 초과할 수 없습니다.")
        @JsonProperty("password")
        String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse extends Response {
        @JsonProperty("token")
        private String token;

        public LoginResponse(Member member, String token) {
            super(member);
            this.token = token;
        }
    }

    @Builder
    @Getter
    @Setter
    public static class RegisterRequest {
        @NotBlank(message = "사용자 ID는 비워둘 수 없습니다")
        @Length(max = 32, message = "아이디는 32 글자를 초과할 수 없습니다.")
        @JsonProperty("userId")
        String userId;

        @NotBlank(message = "사용자 이름은 비워둘 수 없습니다.")
        @Length(max = 32, message = "이름은 32 글자를 초과할 수 없습니다.")
        @JsonProperty("username")
        String username;

        @NotBlank(message = "사용자 비밀번호는 비워둘 수 없습니다.")
        @Length(max = 64, message = "비밀번호는 64 글자를 초과할 수 없습니다.")
        @JsonProperty("password")
        String password;
    }

    @Builder
    @Getter
    @Setter
    public static class UpdateRequest {
        @NotNull(message = "허용되지 않은 수정할 필드입니다.")
        private UpdatableMemberInformation field;

        @NotNull(message = "수정할 값은 비워둘 수 없습니다.")
        private Object value;

        public enum UpdatableMemberInformation {
            USERNAME, PASSWORD
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Basic {
        @JsonProperty("userId")
        String userId;

        @JsonProperty("username")
        String username;

        @JsonProperty("locked")
        boolean locked;

        public Basic(Member member) {
            this.userId = member.getUserId();
            this.username = member.getUsername();
            this.locked = member.isLocked();
        }
    }

    @Getter
    @Setter
    public static class BasicWithTodoLists extends Basic{
        @JsonProperty("todoLists")
        List<TodoListDTO.IDName> todoLists;

        public BasicWithTodoLists(Member member, List<TodoList> todoLists) {
            super(member);
            this.todoLists = todoLists.stream()
                    .map(TodoListDTO.IDName::new)
                    .collect(Collectors.toList());
        }

    }
}
