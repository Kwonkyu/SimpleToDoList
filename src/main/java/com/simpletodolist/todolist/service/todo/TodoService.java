package com.simpletodolist.todolist.service.todo;

import com.simpletodolist.todolist.domain.dto.TodoDTO;
import com.simpletodolist.todolist.domain.dto.TodoListsDTO;
import com.simpletodolist.todolist.domain.dto.TodosDTO;
import com.simpletodolist.todolist.exception.NoMemberFoundException;
import com.simpletodolist.todolist.exception.NoTodoFoundException;
import com.simpletodolist.todolist.exception.NoTodoListFoundException;

public interface TodoService {

    /**
     * Read to-do from member's to-do list.
     * @param memberId Member's user id.
     * @param todoListId To-do list's id.
     * @param todoId To-do's id.
     * @return TodoDTO object filled with to-do.
     * @throws NoMemberFoundException when member with given user id does not exists.
     * @throws NoTodoListFoundException when to-do list doesn't exist.
     * @throws NoTodoFoundException when to-do doesn't exist.
     */
    TodoDTO readTodo(String memberId, long todoListId, long todoId) throws NoMemberFoundException, NoTodoListFoundException, NoTodoFoundException;

    /**
     * Read to-dos from to-do list.
     * @param memberId Member's user id.
     * @param todoListId To-do list's id.
     * @return TodosDTO object filled with to-dos.
     * @throws NoTodoListFoundException when to-do list with given id does not exist.
     */
    TodosDTO readTodosOfTodoList(String memberId, long todoListId) throws NoMemberFoundException, NoTodoListFoundException;

    /**
     * Write to-do on to-do list.
     * @param memberId Member's user id.
     * @param todoListId To-do list's id.
     * @param todoDTO TodoDTO object filled with to-do data.
     * @return TodoDTO object filled with to-do data and generated id.
     */
    TodoDTO writeTodo(String memberId, long todoListId, TodoDTO todoDTO);

    /**
     * Delete to-do from to-do list.
     * @param memberId Member's user id.
     * @param todoListId To-do list's id.
     * @param todoId To-do's id.
     */
    void deleteTodo(String memberId, long todoListId, long todoId);
}
