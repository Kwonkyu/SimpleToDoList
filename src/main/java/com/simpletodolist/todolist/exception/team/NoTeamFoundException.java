package com.simpletodolist.todolist.exception.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoTeamFoundException extends RuntimeException{
    private String message = "No Team Found.";
    private String solution = "Try with different team identification value.";
    // TODO: 반복되는 문자열은 별도의 클래스로.
}
