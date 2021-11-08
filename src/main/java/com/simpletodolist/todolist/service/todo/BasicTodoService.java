package com.simpletodolist.todolist.service.todo;

import com.simpletodolist.todolist.controller.bind.todo.TodoInformationRequest;
import com.simpletodolist.todolist.domain.bind.TodoDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Todo;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.repository.TodoRepository;
import com.simpletodolist.todolist.util.EntityFinder;
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
        Todo newTodo = new Todo(request.getTitle(), request.getContent(), writer, todoList, false);
        todoList.getTodos().add(newTodo);
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
