package com.simpletodolist.todolist.common.exception;

import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.common.exception.FieldErrorDetail;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GeneralExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> authenticationFailed(AuthenticationException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> methodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return ApiResponse.fail(
                String.format("Request parameter type mismatch on '%s'(%s)",
                        exception.getName(),
                        exception.getParameter().getParameter().getParameterizedType().getTypeName()));
        // check get parameter result.
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<FieldErrorDetail> fieldErrorDetails = exception.getFieldErrors().stream()
                .map(fieldError -> new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        String errorMessage = "Request parameter errors on " +
                exception.getFieldErrorCount() +
                " fields. Check result fields for details.";
        return ApiResponse.fail(fieldErrorDetails, errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleIllegalArgument(IllegalArgumentException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleJwtException(JwtException jwtException) {
        return ApiResponse.fail(jwtException.getMessage());
    }
}
