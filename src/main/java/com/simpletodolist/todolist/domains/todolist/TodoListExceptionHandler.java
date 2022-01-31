package com.simpletodolist.todolist.domains.todolist;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.todolist.adapter.repository.NoTodoListFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class TodoListExceptionHandler {

    @ExceptionHandler(NoTodoListFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> noTodoListFound(NoTodoListFoundException exception){
        return ApiResponse.fail(exception.getMessage());
    }
}
