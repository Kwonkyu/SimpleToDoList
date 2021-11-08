package com.simpletodolist.todolist.service.todolist;

import com.simpletodolist.todolist.controller.bind.todolist.TodoListInformationRequest;
import com.simpletodolist.todolist.domain.bind.TodoListDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.team.TeamAccessException;
import com.simpletodolist.todolist.exception.todolist.TodoListAccessException;
import com.simpletodolist.todolist.repository.TodoListRepository;
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

    private void authorizeTodoListOwner(TodoList todoList, Member member) {
        if (!todoList.getOwner().equals(member)) {
            throw new TodoListAccessException(todoList);
        }
    }

    public TodoListDTO updateTodoList(long todoListId, String username, TodoListInformationRequest request) {
        TodoList todoList = entityFinder.findTodoListById(todoListId);
        if (todoList.isLocked()) {
            Member member = entityFinder.findMemberByUsername(username);
            authorizeTodoListOwner(todoList, member);
        }

        todoList.changeName(request.getTodoListName());
        if (request.isLocked()) todoList.lock();
        else todoList.unlock();
        return new TodoListDTO(todoList);
    }

    public void deleteTodoList(long todoListId, String username) {
        TodoList todoList = entityFinder.findTodoListById(todoListId);
        if (todoList.isLocked()) {
            Member member = entityFinder.findMemberByUsername(username);
            authorizeTodoListOwner(todoList, member);
        }

        todoList.getTodos().forEach(todoRepository::delete);
        todoListRepository.delete(todoList);
    }
}
