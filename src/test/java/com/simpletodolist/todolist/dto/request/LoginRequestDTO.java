package com.simpletodolist.todolist.dto.request;

public class LoginRequestDTO {
    public String userId;
    public String password;

    public LoginRequestDTO(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
