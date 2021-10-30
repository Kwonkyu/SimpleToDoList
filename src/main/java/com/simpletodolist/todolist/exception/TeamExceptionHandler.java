package com.simpletodolist.todolist.exception;

import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.exception.team.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class TeamExceptionHandler {
    @ExceptionHandler(NoTeamFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> noTeamFound(NoTeamFoundException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(TeamAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> lockedTeam(TeamAccessException exception) {
        return ApiResponse.fail(exception.getMessage());
    }
}
