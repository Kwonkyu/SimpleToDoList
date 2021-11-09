package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.controller.bind.todo.TodoInformationRequest;
import com.simpletodolist.todolist.domain.bind.TodoDTO;
import com.simpletodolist.todolist.service.todo.BasicTodoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TodoTestMaster {
    private final BasicTodoService todoService;
    private final static Map<Long, TodoDTO> todos = new HashMap<>();

    public TodoTestMaster(BasicTodoService todoService) {
        this.todoService = todoService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length % 32);
    }

    public TodoDTO createNewTodo(String userId, long teamId, long todoListId, boolean locked) {
        return createNewTodo(userId, todoListId, randomString(16), randomString(32) + randomString(32), locked);
    }

    public TodoDTO createNewTodo(String userId, long todoListId, String title, String content, boolean locked) {
        TodoInformationRequest request = new TodoInformationRequest();
        request.setContent(content);
        request.setTitle(title);
        request.setLocked(locked);

        TodoDTO result = todoService.createTodo(userId, todoListId, request);
        todos.put(result.getId(), result);
        return result;
    }

    public TodoDTO getTodoInfo(long todoId) {
        return todos.get(todoId);
    }
}
