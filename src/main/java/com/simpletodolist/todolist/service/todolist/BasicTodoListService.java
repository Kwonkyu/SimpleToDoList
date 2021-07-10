package com.simpletodolist.todolist.service.todolist;

import com.simpletodolist.todolist.domain.UpdatableTodoListInformation;
import com.simpletodolist.todolist.domain.dto.TodoListDTO;
import com.simpletodolist.todolist.domain.dto.TodoListsDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.TeamRepository;
import com.simpletodolist.todolist.repository.TodoListRepository;
import com.simpletodolist.todolist.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicTodoListService implements TodoListService{

    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;
    private final TeamRepository teamRepository;


    @Override
    public boolean isTodoListLocked(long todoListId) throws NoTodoListFoundException {
        return todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new).isLocked();
    }

    @Override
    @Transactional(readOnly = true)
    public TodoListDTO getTodoListDetail(long todoListId) throws NoTodoListFoundException {
        return new TodoListDTO(todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new));
    }

    @Override
    public TodoListDTO createTodoList(long teamId, String memberUserId, TodoListDTO todoListDTO) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        TodoList todoList = new TodoList(todoListDTO.getTodoListName(), team, member);
        todoListRepository.save(todoList);
        return new TodoListDTO(todoList);
    }

    @Override
    public TodoListDTO updateTodoList(long todoListId, UpdatableTodoListInformation field, Object value) throws NoTodoListFoundException {
        TodoList todoList = todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new);
        switch (field) {
            case NAME:
                todoList.changeName((String) value);
                break;

            case LOCKED:
                boolean lock = (boolean) value;
                if (lock) {
                    todoList.lock();
                } else {
                    todoList.unlock();
                }
                break;
        }

        return new TodoListDTO(todoList);
    }

    @Override
    public void deleteTodoList(long todoListId) throws NoTodoListFoundException {
        TodoList todoList = todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new);
        todoList.getTodos().forEach(todoRepository::delete);
        todoListRepository.delete(todoList);
    }


}
