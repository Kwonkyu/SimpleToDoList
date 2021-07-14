package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.domain.bind.TodoListDTO;
import com.simpletodolist.todolist.service.todolist.TodoListService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.simpletodolist.todolist.domain.bind.TodoListDTO.*;

public class TodoListTestMaster {

    private final static Map<Long, Response> todolists = new HashMap<>();

    private final TodoListService todoListService;

    public TodoListTestMaster(TodoListService todoListService) {
        this.todoListService = todoListService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length % 32);
    }

    public Response createNewTodoList(String userId, long teamId) {
        return createNewTodoList(userId, teamId, randomString(31));
    }
    public Response createNewTodoList(String userId, long teamId, String todoListName) {
        RegisterRequest todoListDTO = RegisterRequest.builder().todoListName(todoListName).build();
        Response result = todoListService.createTodoList(teamId, userId, todoListDTO);
        todolists.put(result.getTodoListId(), result);
        return result;
    }

    public Response getTodoListInfo(long todoListId) {
        return todolists.get(todoListId);
    }
}
