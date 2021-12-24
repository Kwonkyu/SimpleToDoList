package com.simpletodolist.todolist.domains.member.exception;

import lombok.Getter;

@Getter
public class DuplicatedMemberException extends RuntimeException {
    public DuplicatedMemberException(String username) {
        super(String.format("Username %s already exists. Try another username.", username));
    }
}
