package com.simpletodolist.todolist.service.todo;

import com.simpletodolist.todolist.domain.dto.TodoDTO;
import com.simpletodolist.todolist.domain.dto.TodosDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Todo;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.NoMemberFoundException;
import com.simpletodolist.todolist.exception.NoTodoFoundException;
import com.simpletodolist.todolist.exception.NoTodoListFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicTodoService implements TodoService{

    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;


    private TodoList findTodoListOfMember(String memberId, long todoListId) {
        Member member = memberRepository.findByUserId(memberId).orElseThrow(NoMemberFoundException::new);
        return member.getTodoLists().stream().filter(t -> t.getId() == todoListId).findAny().orElseThrow(NoTodoListFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public TodoDTO readTodo(String memberId, long todoListId, long todoId) throws NoMemberFoundException, NoTodoListFoundException, NoTodoFoundException {
        TodoList todoList = findTodoListOfMember(memberId, todoListId);
        Todo todo = todoList.getTodos().stream().filter(t -> t.getId() == todoId).findAny().orElseThrow(NoTodoFoundException::new);
        return new TodoDTO(todo);
    }


    @Override
    @Transactional(readOnly = true)
    public TodosDTO readTodosOfTodoList(String memberId, long todoListId) throws NoMemberFoundException, NoTodoListFoundException {
        TodoList todoList = findTodoListOfMember(memberId, todoListId);
        return new TodosDTO(todoList.getTodos());
    }


    @Override
    public TodoDTO writeTodo(String memberId, long todoListId, TodoDTO todo) {
        TodoList todoList = findTodoListOfMember(memberId, todoListId);
        Todo newTodo = new Todo(todo.getTitle(), todo.getContent(), todoList);
        todoRepository.save(newTodo);
        return new TodoDTO(newTodo);
    }

    @Override
    public void deleteTodo(String memberId, long todoListId, long todoId) {
        TodoList todoList = findTodoListOfMember(memberId, todoListId);
        todoRepository.delete(todoList.getTodos().stream().filter(t -> t.getId() == todoId).findAny().orElseThrow(NoTodoFoundException::new));
    }
}
