package com.simpletodolist.todolist.service.todo;

import com.simpletodolist.todolist.domain.dto.TodoDTO;
import com.simpletodolist.todolist.domain.dto.TodosDTO;
import com.simpletodolist.todolist.exception.general.AuthorizationFailedException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;

public interface TodoService {


    /**
     * Authorize if member can access to-do list or not.
     * @param memberUserId Member's user id.
     * @param todoId To-do's id.
     * @throws NoTodoFoundException when to-do list with given id not found.
     * @throws AuthorizationFailedException when member is unauthorized for this to-do.
     */
    void authorizeMember(String memberUserId, long todoId) throws NoTodoFoundException, AuthorizationFailedException;

    /**
     * Read to-do from member's to-do list.
     * @param todoId To-do's id.
     * @return TodoDTO object filled with to-do.
     * @throws NoTodoFoundException when to-do doesn't exist.
     */
    TodoDTO readTodo(long todoId) throws NoTodoFoundException;

    /**
     * Read to-dos from to-do list.
     * @param todoListId To-do list's id.
     * @return TodosDTO object filled with to-dos.
     * @throws NoTodoListFoundException when to-do list with given id does not exist.
     */
    TodosDTO readTodosOfTodoList(long todoListId) throws NoTodoListFoundException;

    /**
     * Write to-do on to-do list.
     * @param todoListId To-do list's id.
     * @param todoDTO TodoDTO object filled with to-do data.
     * @return TodoDTO object filled with to-do data and generated id.
     */
    TodoDTO writeTodo(String memberUserId, long todoListId, TodoDTO todoDTO) throws NoMemberFoundException, NoTodoListFoundException;

    /**
     * Delete to-do from to-do list.
     * @param todoId To-do's id.
     * @throws NoTodoFoundException when to-do with given id doesn't exists.
     */
    void deleteTodo(long todoId) throws NoTodoFoundException;
}
