package com.simpletodolist.todolist.exception;

import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TodoListExceptionHandler {

    @ExceptionHandler(NoTodoListFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ExceptionResponseDTO noTodoListFound(NoTodoListFoundException exception){
        return new ExceptionResponseDTO(exception.getMessage(), exception.getSolution());
    }

}
