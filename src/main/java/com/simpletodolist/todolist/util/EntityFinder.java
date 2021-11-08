package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.domain.entity.Todo;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.TeamRepository;
import com.simpletodolist.todolist.repository.TodoListRepository;
import com.simpletodolist.todolist.repository.TodoRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
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
