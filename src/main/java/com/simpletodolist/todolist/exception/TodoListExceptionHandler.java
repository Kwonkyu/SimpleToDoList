package com.simpletodolist.todolist.exception;

import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import com.simpletodolist.todolist.exception.todolist.TodoListAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class TodoListExceptionHandler {
    @ExceptionHandler(NoTodoListFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> noTodoListFound(NoTodoListFoundException exception){
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(TodoListAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> notOwnerTodoList(TodoListAccessException exception) {
        return ApiResponse.fail(exception.getMessage());
    }
}
