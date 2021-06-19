package com.simpletodolist.todolist.service.todolist;

import com.simpletodolist.todolist.domain.dto.TodoListDTO;
import com.simpletodolist.todolist.domain.dto.TodoListsDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.TodoListRepository;
import com.simpletodolist.todolist.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicTodoListService implements TodoListService{

    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;


    private TodoList findMembersTodoList(String memberUserId, long todoListId) {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        return member.getTodoLists().stream().filter(t -> t.getId() == todoListId).findAny().orElseThrow(NoTodoListFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public TodoListDTO getTodoListDetail(String memberUserId, long todoListId) throws NoMemberFoundException, NoTodoListFoundException {
        return new TodoListDTO(findMembersTodoList(memberUserId, todoListId));
    }

    @Override
    @Transactional(readOnly = true)
    public TodoListsDTO readTodoListsOfMember(String memberId) throws NoMemberFoundException {
        Member member = memberRepository.findByUserId(memberId).orElseThrow(NoMemberFoundException::new);
        List<TodoList> todoLists = todoListRepository.findAllByOwner(member);
        return new TodoListsDTO(todoLists);
    }

    @Override
    public TodoListDTO createTodoList(String memberUserId, TodoListDTO todoListDTO) {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        TodoList todoList = new TodoList(todoListDTO.getTodoListName(), member);
        todoListRepository.save(todoList);
        return new TodoListDTO(todoList);
    }

    @Override
    public void deleteTodoList(String memberUserId, long todoListId) throws NoTodoListFoundException {
        TodoList todoList = findMembersTodoList(memberUserId, todoListId);
        todoList.getTodos().forEach(todoRepository::delete);
        todoListRepository.delete(todoList);
    }


}
