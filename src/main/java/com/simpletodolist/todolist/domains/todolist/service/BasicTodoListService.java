package com.simpletodolist.todolist.domains.todolist.service;

import com.simpletodolist.todolist.domains.todolist.bind.request.TodoListInformationRequest;
import com.simpletodolist.todolist.domains.todolist.bind.TodoListDTO;
import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.team.entity.Team;
import com.simpletodolist.todolist.domains.todolist.entity.TodoList;
import com.simpletodolist.todolist.domains.team.exception.TeamAccessException;
import com.simpletodolist.todolist.domains.todolist.repository.TodoListRepository;
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
public class BasicTodoListService {
    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;
    private final EntityFinder entityFinder;


    @Transactional(readOnly = true)
    public TodoListDTO getTodoListDetail(long todoListId) {
        return new TodoListDTO(entityFinder.findTodoListById(todoListId));
    }

    @Transactional(readOnly = true)
    public List<TodoListDTO> listTodoList(long teamId) {
        Team team = entityFinder.findTeamById(teamId);
        return team.getTodoLists().stream()
                .map(TodoListDTO::new)
                .collect(Collectors.toList());
    }

    public TodoListDTO createTodoList(long teamId, String username, TodoListInformationRequest request) {
        Team team = entityFinder.findTeamById(teamId);
        Member member = entityFinder.findMemberByUsername(username);
        if (!team.isMemberIncluded(member)) {
            throw new TeamAccessException(team);
        }

        TodoList todoList = TodoList.builder()
                .team(team)
                .locked(request.isLocked())
                .name(request.getTodoListName())
                .owner(member).build();
        return new TodoListDTO(todoListRepository.save(todoList));
    }

    public TodoListDTO updateTodoList(long todoListId, TodoListInformationRequest request) {
        TodoList todoList = entityFinder.findTodoListById(todoListId);
        todoList.changeName(request.getTodoListName());
        if (request.isLocked()) todoList.lock();
        else todoList.unlock();
        return new TodoListDTO(todoList);
    }

    public void deleteTodoList(long todoListId) {
        TodoList todoList = entityFinder.findTodoListById(todoListId);
        todoList.getTodos().forEach(todoRepository::delete);
        todoList.getTeam().getTodoLists().remove(todoList);
        todoListRepository.delete(todoList);
    }
}
