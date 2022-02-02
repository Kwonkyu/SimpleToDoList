package com.simpletodolist.todolist.common.exception;

import com.simpletodolist.todolist.common.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionHandler {

	@ExceptionHandler({
		AuthenticationException.class
	})
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<Object> handleAuthentication(AuthenticationException exception) {
		return ApiResponse.fail(exception.getMessage());
	}

	@ExceptionHandler({
		AccessDeniedException.class
	})
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiResponse<Object> handleAccessDenied(AccessDeniedException exception) {
		return ApiResponse.fail(exception.getMessage());
	}

	@ExceptionHandler({
		IllegalArgumentException.class,
		IllegalStateException.class,
		HttpRequestMethodNotSupportedException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<Object> handleIllegals(RuntimeException ex) {
		return ApiResponse.fail(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<Object> methodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
		return ApiResponse.fail(
			String.format(
				"Request parameter type mismatch on '%s'(%s)",
				exception.getName(),
				exception.getParameter()
						 .getParameter()
						 .getParameterizedType()
						 .getTypeName()
			));
		// check get parameter result.
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		List<FieldErrorDetail> fieldErrorDetails = exception.getFieldErrors()
															.stream()
															.map(FieldErrorDetail::of)
															.collect(Collectors.toList());
		String errorMessage = String.format(
			"Request parameter errors on %d fields. Check result fields for details.",
			fieldErrorDetails.size()
		);
		return ApiResponse.fail(fieldErrorDetails, errorMessage);
	}

	@ExceptionHandler({
		RuntimeException.class,
		Exception.class
	})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<Object> handleNotHandled(Exception ex) {
		log.error(
			"unhandled exception {}: {}", ex.getClass()
											.getName(), ex.getMessage());
		ex.printStackTrace();
		return ApiResponse.fail("critical error: please check server logs");
	}

}
