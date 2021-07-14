package com.simpletodolist.todolist.service.todolist;

import com.simpletodolist.todolist.domain.bind.TodoListDTO;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.team.NotJoinedTeamException;
import com.simpletodolist.todolist.exception.todolist.LockedTodoListException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;

import static com.simpletodolist.todolist.domain.bind.TodoListDTO.RegisterRequest;
import static com.simpletodolist.todolist.domain.bind.TodoListDTO.Response;

public interface TodoListService {

    /**
     * Check whether to-do list is locked or not.
     * @param todoListId To-do list's id.
     * @return Boolean value indicating to-do list is locked or not
     * @throws NoTodoListFoundException when to-do list with given it not found.
     */
    boolean isTodoListLocked(long todoListId) throws NoTodoListFoundException;

    /**
     * Get information of to-do list.
     * @param todoListId To-do list's id.
     * @return TodoListDTO object containing information of to-do list.
     * @throws NoTodoListFoundException when to-do list does not exists.
     */
    Response getTodoListDetail(long todoListId) throws NoTodoListFoundException;


    /**
     * Create to-do list of member.
     * @param teamId Team's id.
     * @param memberUserId Member's user id.
     * @param todoListDTO Information of to-do list.
     * @return TodoListDTO object containing
     * @throws NoMemberFoundException when member with given id doesn't exists.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     * @throws NotJoinedTeamException when member is not joined on team.
     */
    Response createTodoList(long teamId, String memberUserId, RegisterRequest todoListDTO) throws NoTeamFoundException, NoMemberFoundException, NotJoinedTeamException;

    /**
     * Update to-do list's information.
     * @param todoListId To-do list's id.
     * @param field Field to update.
     * @param value Updated value.
     * @return TodoListDTO object filled with updated to-do list.
     * @throws NoTodoListFoundException when to-do list with given id not found.
     */
    Response updateTodoList(long todoListId, TodoListDTO.UpdateRequest.UpdatableTodoListInformation field, Object value)
            throws NoTodoListFoundException, LockedTodoListException;

    /**
     * Delete to-do list of member.
     * @param todoListId To-do list's id.
     * @throws NoTodoListFoundException when to-do list with given id doesn't exists.
     */
    void deleteTodoList(long todoListId) throws NoTodoListFoundException;
}
