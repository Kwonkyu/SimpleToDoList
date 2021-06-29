package com.simpletodolist.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {
    @JsonProperty("token")
    private String token;
}
