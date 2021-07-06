package com.simpletodolist.todolist.dto.request;

public class RegisterRequestDTO {
    public String userId;
    public String username;
    public String password;

    public RegisterRequestDTO(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
}
