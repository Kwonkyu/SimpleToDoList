package com.simpletodolist.todolist.domain.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Todo;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class TodoDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("id")
        public long id;

        @JsonProperty("title")
        public String title;

        @JsonProperty("content")
        public String content;

        @JsonProperty("locked")
        public boolean locked;

        public Response(Todo todo) {
            this.id = todo.getId();
            this.title = todo.getTitle();
            this.content = todo.getContent();
            this.locked = todo.isLocked();
        }
    }

    @Getter
    @Setter
    @Builder
    public static class Update {
        @NotNull(message = "허용되지 않은 수정할 항목입니다.")
        private UpdatableTodoInformation field;

        @NotNull(message = "수정할 값은 비워둘 수 없습니다.")
        private Object value;

        public enum UpdatableTodoInformation {
            TITLE, CONTENT, LOCKED
        }
    }

    @Getter
    @Setter
    @Builder
    public static class Create {
        @NotBlank(message = "할 일의 제목은 비워둘 수 없습니다.")
        @Length(max = 64, message = "할 일의 제목은 64 글자를 초과할 수 없습니다.")
        @JsonProperty("title")
        public String title;

        @NotBlank(message = "할 일의 내용은 비워둘 수 없습니다.")
        @Length(max = 1024, message = "할 일의 내용은 1024 글자를 초과할 수 없습니다.")
        @JsonProperty("content")
        public String content;
    }
}
