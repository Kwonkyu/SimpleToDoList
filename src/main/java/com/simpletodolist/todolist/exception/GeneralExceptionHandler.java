package com.simpletodolist.todolist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
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

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ExceptionResponseDTO locked(LockedException exception) {
        return new ExceptionResponseDTO("Locked Account", "Account is locked. Please contact account manager.");
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

}
