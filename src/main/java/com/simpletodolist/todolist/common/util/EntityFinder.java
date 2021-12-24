package com.simpletodolist.todolist.common.util;

import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.team.entity.Team;
import com.simpletodolist.todolist.domains.todo.entity.Todo;
import com.simpletodolist.todolist.domains.todolist.entity.TodoList;
import com.simpletodolist.todolist.domains.member.exception.NoMemberFoundException;
import com.simpletodolist.todolist.domains.team.exception.NoTeamFoundException;
import com.simpletodolist.todolist.domains.todo.exception.NoTodoFoundException;
import com.simpletodolist.todolist.domains.todolist.exception.NoTodoListFoundException;
import com.simpletodolist.todolist.domains.member.repository.MemberRepository;
import com.simpletodolist.todolist.domains.team.repository.TeamRepository;
import com.simpletodolist.todolist.domains.todolist.repository.TodoListRepository;
import com.simpletodolist.todolist.domains.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntityFinder {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;

    public Team findTeamById(long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new NoTeamFoundException(teamId));
    }

    public Member findMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
    }

    public Todo findTodoById(long todoId) {
        return todoRepository.findById(todoId).orElseThrow(() -> new NoTodoFoundException(todoId));
    }

    public TodoList findTodoListById(long todoListId) {
        return todoListRepository.findById(todoListId).orElseThrow(() -> new NoTodoListFoundException(todoListId));
    }
}
