package com.simpletodolist.todolist.service.todo;

import com.simpletodolist.todolist.domain.UpdatableTodoInformation;
import com.simpletodolist.todolist.domain.dto.TodoDTO;
import com.simpletodolist.todolist.domain.dto.TodosDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Todo;
import com.simpletodolist.todolist.domain.entity.TodoList;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.TodoListRepository;
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
    private final TodoListRepository todoListRepository;


    @Override
    public boolean isTodoLocked(long todoId) throws NoTodoFoundException {
        return todoRepository.findById(todoId).orElseThrow(NoTodoFoundException::new).isLocked();
    }

    @Override
    @Transactional(readOnly = true)
    public TodoDTO readTodo(long todoId) throws NoTodoFoundException {
        return new TodoDTO(todoRepository.findById(todoId).orElseThrow(NoTodoFoundException::new));
    }


    @Override
    @Transactional(readOnly = true)
    public TodosDTO readTodosOfTodoList(long todoListId) throws NoTodoListFoundException {
        return new TodosDTO(todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new).getTodos());
    }


    @Override
    public TodoDTO writeTodo(String memberUserId, long todoListId, TodoDTO todo) {
        Member writer = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        TodoList todoList = todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new);
        Todo newTodo = new Todo(todo.getTitle(), todo.getContent(), writer, todoList);
        todoRepository.save(newTodo);
        return new TodoDTO(newTodo);
    }

    @Override
    public TodoDTO updateTodo(long todoId, UpdatableTodoInformation field, Object value) throws NoTodoFoundException {
        Todo todo = todoRepository.findById(todoId).orElseThrow(NoTodoFoundException::new);
        switch (field) {
            case LOCKED:
                boolean lock = (boolean) value;
                if(lock) {
                    todo.lock();
                } else {
                    todo.unlock();
                }
                break;

            case CONTENT:
                todo.changeContent((String) value);
                break;

            case TITLE:
                todo.changeTitle((String) value);
                break;
        }

        return new TodoDTO(todo);
    }

    @Override
    public void deleteTodo(long todoId) {
        todoRepository.deleteById(todoId);
    }
}
