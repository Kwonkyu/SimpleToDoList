package com.simpletodolist.todolist.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldErrorDetail {
    private String rejectedField;
    private String cause;
}
