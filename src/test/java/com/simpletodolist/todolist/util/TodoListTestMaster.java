package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.domain.dto.TodoListDTO;
import com.simpletodolist.todolist.service.todolist.TodoListService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TodoListTestMaster {

    private final static Map<Long, TodoListDTO> todolists = new HashMap<>();

    private final TodoListService todoListService;

    public TodoListTestMaster(TodoListService todoListService) {
        this.todoListService = todoListService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length % 32);
    }

    public TodoListDTO createNewTodoList(String userId, long teamId) {
        return createNewTodoList(userId, teamId, randomString(31));
    }
    public TodoListDTO createNewTodoList(String userId, long teamId, String todoListName) {
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setTodoListName(todoListName);
        TodoListDTO result = todoListService.createTodoList(teamId, userId, todoListDTO);
        todolists.put(result.getTodoListId(), result);
        return result;
    }

    public TodoListDTO getTodoListInfo(long todoListId) {
        return todolists.get(todoListId);
    }
}
