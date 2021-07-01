package com.simpletodolist.todolist.exception;

import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TodoExceptionHandler {

    @ExceptionHandler(NoTodoFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ExceptionResponseDTO noTodoFound(NoTodoFoundException exception){
        return new ExceptionResponseDTO(exception.getError(), exception.getMessage());
    }
}
