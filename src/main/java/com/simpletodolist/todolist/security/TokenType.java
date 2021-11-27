package com.simpletodolist.todolist.security;

public enum TokenType {
    BEARER("Bearer");

    final String name;

    TokenType(String name) {
        this.name = name;
    }
}
