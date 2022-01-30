package com.simpletodolist.todolist.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse <T> {
    private final boolean success;
    private final T result;
    private final String message;

    public static <U> ApiResponse<U> success(U result) {
        return new ApiResponse<>(true, result, "");
    }

    public static <U> ApiResponse<U> success(U result, String message) {
        return new ApiResponse<>(true, result, message);
    }

    public static <U> ApiResponse<U> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }

    public static <U> ApiResponse<U> fail(U result, String message) {
        return new ApiResponse<>(false, result, message);
    }
}
