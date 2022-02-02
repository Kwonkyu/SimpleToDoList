package com.simpletodolist.todolist.domains.user;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.user.adapter.repository.UserNotFoundException;
import com.simpletodolist.todolist.domains.user.service.port.ForbiddenTeamException;
import com.simpletodolist.todolist.domains.user.service.port.IllegalTeamRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<Object> userNotFound(
		UserNotFoundException exception
	) {
		return ApiResponse.fail(exception.getMessage());
	}

	@ExceptionHandler(ForbiddenTeamException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiResponse<Object> forbiddenTeam(
		ForbiddenTeamException exception
	) {
		return ApiResponse.fail(exception.getMessage());
	}

	@ExceptionHandler(IllegalTeamRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<Object> illegalTeamRequest(
		IllegalTeamRequestException exception
	) {
		return ApiResponse.fail(exception.getMessage());
	}
}
