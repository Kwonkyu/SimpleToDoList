package com.simpletodolist.todolist.domains.member.exception;

import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.member.exception.DuplicatedMemberException;
import com.simpletodolist.todolist.domains.member.exception.DuplicatedTeamJoinException;
import com.simpletodolist.todolist.domains.member.exception.NoMemberFoundException;
import com.simpletodolist.todolist.domains.member.exception.NotJoinedTeamException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class MemberExceptionHandler {
    @ExceptionHandler(NoMemberFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> noMemberFound(NoMemberFoundException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(DuplicatedMemberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> duplicatedMember(DuplicatedMemberException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(DuplicatedTeamJoinException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> duplicatedTeamJoin(DuplicatedTeamJoinException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(NotJoinedTeamException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> notJoinedMember(NotJoinedTeamException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> lockedMember(LockedException exception) {
        return ApiResponse.fail(exception.getMessage());
    }
}
