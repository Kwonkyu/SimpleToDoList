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
public class TodoListDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("id")
        long todoListId;

        @JsonProperty("ownerUserId")
        String ownerUserId;

        @JsonProperty("ownerUsername")
        String ownerUsername;

        @JsonProperty("name")
        String todoListName;

        @JsonProperty("todos")
        List<TodoDTO.Response> todos;

        @JsonProperty("locked")
        boolean locked;

        public Response(TodoList todoList) {
            this.todoListId = todoList.getId();
            Member owner = todoList.getOwner();
            this.ownerUserId = owner.getUserId();
            this.ownerUsername = owner.getUsername();
            this.todoListName = todoList.getName();
            this.todos = todoList.getTodos().stream().map(TodoDTO.Response::new).collect(Collectors.toList());
            this.locked = todoList.isLocked();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "할 일 리스트의 이름은 비워둘 수 없습니다.")
        @Length(max = 64, message = "할 일 리스트의 이름은 64 글자를 초과할 수 없습니다.")
        @JsonProperty("name")
        public String todoListName;
    }

    @Getter
    @Setter
    @Builder
    public static class UpdateRequest {
        @NotNull(message = "허용되지 않은 수정할 필드입니다.")
        private UpdatableTodoListInformation field;

        @NotNull(message = "수정할 값은 비워둘 수 없습니다.")
        private Object value;

        public enum UpdatableTodoListInformation {
            NAME, LOCKED
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class IDName {
        @JsonProperty("id")
        public long todoListId;

        @JsonProperty("name")
        public String todoListName;

        public IDName(TodoList todoList) {
            this.todoListId = todoList.getId();
            this.todoListName = todoList.getName();
        }
    }
}
