package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.controller.bind.TodoDTO;
import com.simpletodolist.todolist.service.todo.TodoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TodoTestMaster {

    private final static Map<Long, TodoDTO> todos = new HashMap<>();

    private final TodoService todoService;

    public TodoTestMaster(TodoService todoService) {
        this.todoService = todoService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length % 32);
    }

    public TodoDTO createNewTodo(String userId, long teamId, long todoListId) {
        return createNewTodoList(userId, teamId, todoListId, randomString(16), randomString(32) + randomString(32));
    }
    public TodoDTO createNewTodoList(String userId, long teamId, long todoListId, String title, String content) {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle(title);
        todoDTO.setContent(content);
        TodoDTO result = todoService.createTodo(userId, todoListId, todoDTO);
        todos.put(result.getId(), result);
        return result;
    }

    public TodoDTO getTodoInfo(long todoId) {
        return todos.get(todoId);
    }
}