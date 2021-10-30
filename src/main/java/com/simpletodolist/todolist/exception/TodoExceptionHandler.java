package com.simpletodolist.todolist.exception;

import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.exception.todo.TodoAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class TodoExceptionHandler {
    @ExceptionHandler(NoTodoFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> noTodoFound(NoTodoFoundException exception){
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(TodoAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> notWriterTodo(TodoAccessException exception) {
        return ApiResponse.fail(exception.getMessage());
    }
}
