package com.simpletodolist.todolist.exception.util;

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
