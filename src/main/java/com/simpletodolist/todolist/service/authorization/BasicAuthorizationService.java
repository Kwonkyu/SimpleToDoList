package com.simpletodolist.todolist.service.authorization;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.domain.entity.Todo;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.member.NotJoinedTeamException;
import com.simpletodolist.todolist.exception.team.TeamAccessException;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.exception.todo.TodoAccessException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import com.simpletodolist.todolist.exception.todolist.TodoListAccessException;
import com.simpletodolist.todolist.util.EntityFinder;
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
}
