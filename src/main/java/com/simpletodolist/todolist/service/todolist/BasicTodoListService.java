package com.simpletodolist.todolist.service.todolist;

import com.simpletodolist.todolist.domain.bind.TodoListDTO;
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

import static com.simpletodolist.todolist.domain.bind.TodoListDTO.RegisterRequest;
import static com.simpletodolist.todolist.domain.bind.TodoListDTO.Response;

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
    public Response getTodoListDetail(long todoListId) throws NoTodoListFoundException {
        return new Response(todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new));
    }

    @Override
    public Response createTodoList(long teamId, String memberUserId, RegisterRequest todoListDTO) {
        Team team = teamRepository.findById(teamId).orElseThrow(NoTeamFoundException::new);
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        TodoList todoList = new TodoList(todoListDTO.getTodoListName(), team, member);
        todoListRepository.save(todoList);
        return new Response(todoList);
    }

    @Override
    public Response updateTodoList(long todoListId, TodoListDTO.UpdateRequest.UpdatableTodoListInformation field, Object value) throws NoTodoListFoundException {
        TodoList todoList = todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new);
        String changedValue = String.valueOf(value);
        switch (field) {
            case NAME:
                todoList.changeName(changedValue.length() > 64 ? changedValue.substring(0, 64) : changedValue);
                break;

            case LOCKED:
                boolean lock = Boolean.parseBoolean(changedValue);
                if (lock) {
                    todoList.lock();
                } else {
                    todoList.unlock();
                }
                break;
        }

        return new Response(todoList);
    }

    @Override
    public void deleteTodoList(long todoListId) throws NoTodoListFoundException {
        TodoList todoList = todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new);
        todoList.getTodos().forEach(todoRepository::delete);
        todoListRepository.delete(todoList);
    }


}
