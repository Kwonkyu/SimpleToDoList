package com.simpletodolist.todolist.service.authorization;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.domain.entity.Todo;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.team.NotJoinedTeamException;
import com.simpletodolist.todolist.exception.team.NotTeamLeaderException;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.exception.todo.NotTodoWriterException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import com.simpletodolist.todolist.exception.todolist.NotTodoListOwnerException;
import com.simpletodolist.todolist.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthorizationService implements AuthorizationService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TodoListRepository todoListRepository;
    private final TodoRepository todoRepository;
    private final MemberTeamAssocRepository memberTeamAssocRepository;


    private boolean validateTeamLeader(String memberUserId, long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        return validateTeamLeader(memberUserId, team);
    }

    private boolean validateTeamLeader(String memberUserId, Team team) {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        return validateTeamLeader(member, team);
    }

    private boolean validateTeamLeader(Member member, Team team) {
        if(!memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new NotJoinedTeamException();
        return team.getLeader().equals(member);
    }

    @Override
    public void authorizeTeamMember(String memberUserId, long teamId) throws NoMemberFoundException, NoTeamFoundException, NotJoinedTeamException {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        // TODO: 연관관계 엔티티를 찾나 repository에서 찾나 쿼리는 동일한가?
        if(!memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new NotJoinedTeamException();
    }

    @Override
    public void authorizeTeamLeader(String memberUserId, long teamId) throws NoMemberFoundException, NoTeamFoundException, NotTeamLeaderException {
        if(!validateTeamLeader(memberUserId, teamId)) throw new NotTeamLeaderException();
    }

    @Override
    public void authorizeTodoListOwner(String memberUserId, long todoListId) throws NoMemberFoundException, NoTodoListFoundException, NotTodoListOwnerException {
        TodoList todoList = todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        if (!todoList.getOwner().equals(member)) throw new NotTodoListOwnerException();
    }

    @Override
    public void authorizeTodoWriter(String memberUserId, long todoId) throws NoMemberFoundException, NoTodoFoundException, NotTodoWriterException {
        Todo todo = todoRepository.findById(todoId).orElseThrow(NoTodoFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        if (!todo.getWriter().equals(member)) throw new NotTodoWriterException();
    }

    @Override
    public void validateTeamContainsTodoList(long teamId, long todoListId) throws NoTeamFoundException, NoTodoListFoundException {
        TodoList todoList = todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new);
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        if(!todoList.getTeam().equals(team)) throw new NoTodoListFoundException(); // not in team 같은 정보를 노출할 필요는 없을듯.
    }

    @Override
    public void validateTodoListContainsTodo(long todoListId, long todoId) throws NoTodoListFoundException, NoTodoFoundException {
        TodoList todoList = todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new);
        Todo todo = todoRepository.findById(todoId).orElseThrow(NoTodoFoundException::new);
        if(!todo.getTodoList().equals(todoList)) throw new NoTodoFoundException();
    }

    @Override
    public void fullAuthorization(String memberUserId, long teamId) throws NoMemberFoundException, NoTeamFoundException, NotJoinedTeamException, NotTeamLeaderException {
        if(!validateTeamLeader(memberUserId, teamId)) throw new NotTeamLeaderException();
    }

    @Override
    public void fullAuthorization(String memberUserId, long teamId, long todoListId)
            throws NoMemberFoundException, NoTeamFoundException, NoTodoListFoundException, NotJoinedTeamException, NotTodoListOwnerException {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        if(!memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new NotJoinedTeamException();

        TodoList todoList = todoListRepository.findByIdAndTeam(todoListId, team).orElseThrow(NoTodoListFoundException::new);
        if(!validateTeamLeader(member, team) && !todoList.getOwner().equals(member)) throw new NotTodoListOwnerException();
    }

    @Override
    public void fullAuthorization(String memberUserId, long teamId, long todoListId, long todoId)
            throws NoMemberFoundException, NoTeamFoundException, NoTodoListFoundException, NoTodoFoundException,
            NotJoinedTeamException, NotTodoWriterException {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        if(!memberTeamAssocRepository.existsByTeamAndMember(team, member)) throw new NotJoinedTeamException();

        TodoList todoList = todoListRepository.findByIdAndTeam(todoListId, team).orElseThrow(NoTodoListFoundException::new);
        Todo todo = todoRepository.findByIdAndTodoList(todoId, todoList).orElseThrow(NoTodoFoundException::new);
        if(!validateTeamLeader(member, team) && !todo.getWriter().equals(member)) throw new NotTodoWriterException();
    }
}
