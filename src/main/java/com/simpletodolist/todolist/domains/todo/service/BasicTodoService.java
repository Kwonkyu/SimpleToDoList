package com.simpletodolist.todolist.domains.todo.service;

import com.simpletodolist.todolist.domains.todo.bind.request.TodoInformationRequest;
import com.simpletodolist.todolist.domains.todo.bind.response.TodoDTO;
import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.todo.entity.Todo;
import com.simpletodolist.todolist.domains.todolist.entity.TodoList;
import com.simpletodolist.todolist.domains.todo.exception.NoTodoFoundException;
import com.simpletodolist.todolist.domains.todo.repository.TodoRepository;
import com.simpletodolist.todolist.common.util.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicTodoService {
    private final TodoRepository todoRepository;
    private final EntityFinder entityFinder;

    @Transactional(readOnly = true)
    public TodoDTO readTodo(long todoId) throws NoTodoFoundException {
        return new TodoDTO(entityFinder.findTodoById(todoId));
    }

    @Transactional(readOnly = true)
    public List<TodoDTO> readTodosOfTodoList(long todoListId) {
        return entityFinder.findTodoListById(todoListId)
                .getTodos().stream()
                .map(TodoDTO::new)
                .collect(Collectors.toList());
    }

    public TodoDTO createTodo(String username, long todoListId, TodoInformationRequest request) {
        Member writer = entityFinder.findMemberByUsername(username);
        TodoList todoList = entityFinder.findTodoListById(todoListId);
        Todo newTodo = Todo.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(writer)
                .todoList(todoList)
                .locked(request.isLocked()).build();
        return new TodoDTO(todoRepository.save(newTodo));
    }

    public TodoDTO updateTodo(long todoId, TodoInformationRequest request) throws NoTodoFoundException {
        Todo todo = entityFinder.findTodoById(todoId);
        todo.changeTitle(request.getTitle());
        todo.changeContent(request.getContent());
        if(request.isLocked()) todo.lock();
        else todo.unlock();
        return new TodoDTO(todo);
    }

    public void deleteTodo(long todoId) {
        todoRepository.deleteById(todoId);
    }
}
