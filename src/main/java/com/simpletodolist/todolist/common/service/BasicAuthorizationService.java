package com.simpletodolist.todolist.common.service;

import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.team.entity.Team;
import com.simpletodolist.todolist.domains.todo.entity.Todo;
import com.simpletodolist.todolist.domains.todolist.entity.TodoList;
import com.simpletodolist.todolist.domains.member.exception.NotJoinedTeamException;
import com.simpletodolist.todolist.domains.team.exception.TeamAccessException;
import com.simpletodolist.todolist.domains.todo.exception.NoTodoFoundException;
import com.simpletodolist.todolist.domains.todo.exception.TodoAccessException;
import com.simpletodolist.todolist.domains.todolist.exception.NoTodoListFoundException;
import com.simpletodolist.todolist.domains.todolist.exception.TodoListAccessException;
import com.simpletodolist.todolist.common.util.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthorizationService {
    private final EntityFinder entityFinder;

    public void authorizeTeamLeader(long teamId, String username) {
        Team team = entityFinder.findTeamById(teamId);
        Member member = entityFinder.findMemberByUsername(username);
        if(!team.getLeader().equals(member)) {
            throw new TeamAccessException(team);
        }
    }

    public void authorizeTeamMember(long teamId, String username) {
        Team team = entityFinder.findTeamById(teamId);
        Member member = entityFinder.findMemberByUsername(username);
        authorizeTeamMember(team, member);
    }

    public void authorizeTeamMember(Team team, Member member) {
        if(!team.isMemberIncluded(member)) {
            throw new NotJoinedTeamException(member, team);
        }
    }

    public void authorizeTodoList(long teamId, String username, long todoListId) {
        Team team = entityFinder.findTeamById(teamId);
        Member member = entityFinder.findMemberByUsername(username);
        authorizeTeamMember(team, member);

        TodoList todoList = entityFinder.findTodoListById(todoListId);
        if (!team.getTodoLists().contains(todoList)) {
            throw new NoTodoListFoundException(todoListId);
        }
    }

    public void authorizeTodoListUpdate(long teamId, String username, long todoListId) {
        authorizeTodoList(teamId, username, todoListId);
        Team team = entityFinder.findTeamById(teamId);
        if (team.getLeader().getUsername().equals(username)) {
            return;
        }

        TodoList todoList = entityFinder.findTodoListById(todoListId);
        if(todoList.isLocked() && !todoList.getOwner().getUsername().equals(username)) {
            throw new TodoListAccessException(todoList);
        }
    }

    public void authorizeTodoListLock(long teamId, String username, long todoListId) {
        authorizeTodoList(teamId, username, todoListId);
        Team team = entityFinder.findTeamById(teamId);
        if (team.getLeader().getUsername().equals(username)) {
            return;
        }

        TodoList todoList = entityFinder.findTodoListById(todoListId);
        if(!todoList.getOwner().getUsername().equals(username)) {
            throw new TodoListAccessException(todoList);
        }
    }

    public void authorizeTodo(long teamId, String username, long todoListId, long todoId) {
        authorizeTodoList(teamId, username, todoListId);
        TodoList todoList = entityFinder.findTodoListById(todoListId);
        if(todoList.getTodos().stream().noneMatch(todo -> todo.getId() == todoId)) {
            throw new NoTodoFoundException(todoId);
        }
    }

    public void authorizeTodoUpdate(long teamId, String username, long todoListId, long todoId) {
        authorizeTodo(teamId, username, todoListId, todoId);
        Todo todo = entityFinder.findTodoById(todoId);
        Team team = entityFinder.findTeamById(teamId);
        if (team.getLeader().getUsername().equals(username)) {
            return;
        }

        if(todo.isLocked() && !todo.getWriter().getUsername().equals(username)) {
            throw new TodoAccessException(entityFinder.findMemberByUsername(username), todo);
        }
    }

    public void authorizeTodoLock(long teamId, String username, long todoListId, long todoId) {
        authorizeTodo(teamId, username, todoListId, todoId);
        Todo todo = entityFinder.findTodoById(todoId);
        Team team = entityFinder.findTeamById(teamId);
        if (team.getLeader().getUsername().equals(username)) {
            return;
        }

        if(todo.getWriter().getUsername().equals(username)) {
            throw new TodoAccessException(entityFinder.findMemberByUsername(username), todo);
        }
    }
}
