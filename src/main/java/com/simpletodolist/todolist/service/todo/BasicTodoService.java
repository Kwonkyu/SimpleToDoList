package com.simpletodolist.todolist.service.todo;

import com.simpletodolist.todolist.domain.bind.TodoDTO;
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

import java.util.List;
import java.util.stream.Collectors;

import static com.simpletodolist.todolist.domain.bind.TodoDTO.Create;
import static com.simpletodolist.todolist.domain.bind.TodoDTO.Response;

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
    public Response readTodo(long todoId) throws NoTodoFoundException {
        return new Response(todoRepository.findById(todoId).orElseThrow(NoTodoFoundException::new));
    }


    @Override
    @Transactional(readOnly = true)
    public List<Response> readTodosOfTodoList(long todoListId) throws NoTodoListFoundException {
        return todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new).getTodos().stream()
                .map(Response::new).collect(Collectors.toList());
    }


    @Override
    public Response createTodo(String memberUserId, long todoListId, Create todo) {
        Member writer = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        TodoList todoList = todoListRepository.findById(todoListId).orElseThrow(NoTodoListFoundException::new);
        Todo newTodo = new Todo(todo.getTitle(), todo.getContent(), writer, todoList);
        todoRepository.save(newTodo);
        return new TodoDTO.Response(newTodo);
    }

    @Override
    public Response updateTodo(long todoId, TodoDTO.Update.UpdatableTodoInformation field, Object value) throws NoTodoFoundException {
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

        return new Response(todo);
    }

    @Override
    public void deleteTodo(long todoId) {
        todoRepository.deleteById(todoId);
    }
}
