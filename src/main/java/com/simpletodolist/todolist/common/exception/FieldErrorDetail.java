package com.simpletodolist.todolist.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldErrorDetail {

	private final String rejectedField;
	private final String cause;

	public static FieldErrorDetail of(FieldError fieldError) {
		return new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage());
	}
}
