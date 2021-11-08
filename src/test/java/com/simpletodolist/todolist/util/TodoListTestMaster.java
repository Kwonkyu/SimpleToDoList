package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.controller.bind.todolist.TodoListInformationRequest;
import com.simpletodolist.todolist.domain.bind.TodoListDTO;
import com.simpletodolist.todolist.service.todolist.BasicTodoListService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TodoListTestMaster {
    private final BasicTodoListService todoListService;

    private final static Map<Long, TodoListDTO> todolists = new HashMap<>();

    public TodoListTestMaster(BasicTodoListService todoListService) {
        this.todoListService = todoListService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length % 32);
    }

    public TodoListDTO createNewTodoList(String userId, long teamId) {
        return createNewTodoList(userId, teamId, randomString(31));
    }

    public TodoListDTO createNewTodoList(String userId, long teamId, String todoListName) {
        TodoListInformationRequest request = new TodoListInformationRequest();
        request.setTodoListName(todoListName);
        request.setLocked(false);
        TodoListDTO result = todoListService.createTodoList(teamId, userId, request);
        todolists.put(result.getId(), result);
        return result;
    }

    public TodoListDTO getTodoListInfo(long todoListId) {
        return todolists.get(todoListId);
    }
}
