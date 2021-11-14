package com.simpletodolist.todolist.controller.bind;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class JwtRequest {
    @NotBlank(groups = {Access.class, Both.class}, message = "Access token should not be empty.")
    private String accessToken;
    @NotBlank(groups = {Refresh.class, Both.class}, message = "Refresh token should not be empty.")
    private String refreshToken;

    public interface Access {}
    public interface Refresh {}
    public interface Both {}
}

