package com.simpletodolist.todolist.service.todo;

import com.simpletodolist.todolist.domain.UpdatableTodoInformation;
import com.simpletodolist.todolist.domain.dto.TodoDTO;
import com.simpletodolist.todolist.domain.dto.TodosDTO;
import com.simpletodolist.todolist.exception.general.AuthorizationFailedException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;

public interface TodoService {

    /**
     * Check if to-do is locked or not.
     * @param todoId To-do's id.
     * @return Boolean value indicating to-do is locked or not.
     * @throws NoTodoFoundException when to-do with given id not found.
     */
    boolean isTodoLocked(long todoId) throws NoTodoFoundException;

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
    TodoDTO createTodo(String memberUserId, long todoListId, TodoDTO todoDTO) throws NoMemberFoundException, NoTodoListFoundException;

    /**
     * Update to-do element.
     * @param todoId To-do to update.
     * @param field Updating field.
     * @param value Updated value.
     * @return TodoDTO object filled with updated to-do.
     * @throws NoTodoFoundException when to-do with given id doesn't exists.
     */
    TodoDTO updateTodo(long todoId, UpdatableTodoInformation field, Object value) throws NoTodoFoundException;

    /**
     * Delete to-do from to-do list.
     * @param todoId To-do's id.
     * @throws NoTodoFoundException when to-do with given id doesn't exists.
     */
    void deleteTodo(long todoId) throws NoTodoFoundException;

}
