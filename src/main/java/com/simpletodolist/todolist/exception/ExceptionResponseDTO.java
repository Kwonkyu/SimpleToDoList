package com.simpletodolist.todolist.exception;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class ExceptionResponseDTO {

    private final String error;
    private final String message;

}
