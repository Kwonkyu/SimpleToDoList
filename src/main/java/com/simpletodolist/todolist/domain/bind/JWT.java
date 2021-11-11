package com.simpletodolist.todolist.domain.bind;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JWT {
    private final String accessToken;
    private final String refreshToken;
}
