package com.simpletodolist.todolist.exception;

import com.simpletodolist.todolist.exception.member.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler(NoMemberFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ExceptionResponseDTO noMemberFound(NoMemberFoundException exception) {
        return new ExceptionResponseDTO(exception.getError(), exception.getMessage());
    }

    @ExceptionHandler(DuplicatedMemberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponseDTO duplicatedMember(DuplicatedMemberException exception) {
        return new ExceptionResponseDTO(exception.getError(), exception.getMessage());
    }

    @ExceptionHandler(DuplicatedTeamJoinException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponseDTO duplicatedTeamJoin(DuplicatedTeamJoinException exception) {
        return new ExceptionResponseDTO(exception.getError(), exception.getMessage());
    }

    @ExceptionHandler(InvalidTeamWithdrawException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponseDTO invalidTeamWithdraw(InvalidTeamWithdrawException exception) {
        return new ExceptionResponseDTO(exception.getError(), exception.getMessage());
    }

    @ExceptionHandler(NotJoinedMemberException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ExceptionResponseDTO notJoinedMember(NotJoinedMemberException exception) {
        return new ExceptionResponseDTO(exception.getError(), exception.getMessage());
    }

    @ExceptionHandler(LockedMemberException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ExceptionResponseDTO lockedMember(LockedMemberException exception) {
        return new ExceptionResponseDTO(exception.getError(), exception.getMessage());
    }
}
