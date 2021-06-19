package com.simpletodolist.todolist.exception;

import com.simpletodolist.todolist.exception.team.DuplicatedMemberJoinException;
import com.simpletodolist.todolist.exception.team.DuplicatedTeamException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TeamExceptionHandler {

    @ExceptionHandler(NoTeamFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ExceptionResponseDTO noTeamFound(NoTeamFoundException exception) {
        return new ExceptionResponseDTO(exception.getMessage(), exception.getSolution());
    }

    @ExceptionHandler(DuplicatedMemberJoinException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ExceptionResponseDTO duplicatedMemberJoin(DuplicatedMemberJoinException exception) {
        return new ExceptionResponseDTO(exception.getMessage(), exception.getSolution());
    }

    @ExceptionHandler(DuplicatedTeamException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ExceptionResponseDTO duplicatedTeam(DuplicatedTeamException exception) {
        return new ExceptionResponseDTO(exception.getMessage(), exception.getSolution());
    }
}
