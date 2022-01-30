package com.simpletodolist.todolist.domains.team;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.team.adapter.repository.TeamNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class TeamExceptionHandler {

	@ExceptionHandler(TeamNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<Object> handleTeamNotFound(TeamNotFoundException exception) {
		return ApiResponse.fail(exception.getMessage());
	}
}
