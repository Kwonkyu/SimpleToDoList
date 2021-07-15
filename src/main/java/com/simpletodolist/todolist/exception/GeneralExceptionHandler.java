package com.simpletodolist.todolist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ExceptionResponseDTO authenticationFailed(AuthenticationException exception) {
        return new ExceptionResponseDTO("Authentication Failed", exception.getMessage());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponseDTO methodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return new ExceptionResponseDTO("Parameter Type Not Matched.", "Check API requirements.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponseDTO authenticationFailed(MethodArgumentNotValidException exception) {
        return new ExceptionResponseDTO("Parameter Value Constraint Not Matched.", "Check API requirements.");
    }

    // TODO: 현재 spring security에서 permit하지 않아도 헤더를 확인해서 검증하고 있는데 옳은 방법인가?
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponseDTO missingRequestHeader(MissingRequestHeaderException exception) {
        return new ExceptionResponseDTO("Requested Header Not Present", "Check API requirements");
    }
}
