package com.simpletodolist.todolist.dto.request;

public class TodoCreateDTO {
    public String title;
    public String content;

    public TodoCreateDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
