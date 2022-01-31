package com.simpletodolist.todolist.domains.todo;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.todo.adapter.repository.NoTodoFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class TodoExceptionHandler {

    @ExceptionHandler(NoTodoFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> noTodoFound(NoTodoFoundException exception) {
        return ApiResponse.fail(exception.getMessage());
    }
}
