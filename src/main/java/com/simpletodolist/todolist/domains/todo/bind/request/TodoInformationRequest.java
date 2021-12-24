package com.simpletodolist.todolist.domains.todo.bind.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TodoInformationRequest {
    @NotBlank(message = "할 일의 제목은 비워둘 수 없습니다.")
    @Length(max = 64, message = "할 일의 제목은 64 글자를 초과할 수 없습니다.")
    @JsonProperty("title")
    private String title;

    @NotBlank(message = "할 일의 내용은 비워둘 수 없습니다.")
    @Length(max = 1024, message = "할 일의 내용은 1024 글자를 초과할 수 없습니다.")
    @JsonProperty("content")
    private String content;

    @JsonProperty("locked")
    private boolean locked;
}
