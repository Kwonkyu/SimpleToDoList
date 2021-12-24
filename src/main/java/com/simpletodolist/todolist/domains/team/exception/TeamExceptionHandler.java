package com.simpletodolist.todolist.domains.team.exception;

import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.team.exception.NoTeamFoundException;
import com.simpletodolist.todolist.domains.team.exception.TeamAccessException;
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
