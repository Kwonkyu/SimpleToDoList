package com.simpletodolist.todolist.exception.member;


import com.simpletodolist.todolist.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoMemberFoundException extends RuntimeException{
    public static final String DEFAULT_ERROR = Member.NO_MEMBER_FOUND;
    public static final String DEFAULT_MESSAGE = "Try with different member identification value.";

    private String error = DEFAULT_ERROR;
    private String message = DEFAULT_MESSAGE;
}
