package com.simpletodolist.todolist.domains.member.exception;


import lombok.Getter;

@Getter
public class NoMemberFoundException extends RuntimeException{
    public NoMemberFoundException(long id) {
        super(String.format("Member with userid %d not found.", id));
    }

    public NoMemberFoundException(String username) {
        super(String.format("Member with username %s not found.", username));
    }
}
