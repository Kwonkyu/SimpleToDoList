package com.simpletodolist.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Todo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class TodoDTO {

    @JsonProperty("id")
    private long id;

    @NotBlank
    @Length(max = 64)
    @JsonProperty("title")
    private String title;

    @NotBlank
    @Length(max = 255)
    @JsonProperty("content")
    private String content;

    public TodoDTO(Todo todo){
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.content = todo.getContent();
    }
}
