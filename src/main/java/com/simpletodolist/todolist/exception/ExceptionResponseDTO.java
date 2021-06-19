package com.simpletodolist.todolist.exception;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class ExceptionResponseDTO {

    private final String message;
    private final String solution;

}
