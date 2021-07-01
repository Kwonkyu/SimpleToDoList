package com.simpletodolist.todolist.service.todolist;

import com.simpletodolist.todolist.domain.UpdatableTodoListInformation;
import com.simpletodolist.todolist.domain.dto.TodoListDTO;
import com.simpletodolist.todolist.domain.dto.TodoListsDTO;
import com.simpletodolist.todolist.exception.general.AuthorizationFailedException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;

public interface TodoListService {

    /**
     * Authorize if member can access to-do list or not.
     * @param memberUserId Member's user id.
     * @param todoListId To-do list's id.
     * @throws NoTodoListFoundException when to-do list with given id not found.
     * @throws AuthorizationFailedException when member is not authorized with this to-do list.
     */
    void authorizeMember(String memberUserId, long todoListId) throws NoTodoListFoundException, AuthorizationFailedException;


    /**
     * Get information of to-do list.
     * @param todoListId To-do list's id.
     * @return TodoListDTO object containing information of to-do list.
     * @throws NoTodoListFoundException when to-do list does not exists.
     */
    TodoListDTO getTodoListDetail(long todoListId) throws NoTodoListFoundException;


    /**
     * Read to-do lists of team.
     * @param teamId Team's id.
     * @return TodoListsDTO object filled with team's to-do lists.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     */
    TodoListsDTO getTodoListsOfTeam(long teamId) throws NoTeamFoundException;


    /**
     * Create to-do list of member.
     * @param teamId Team's id.
     * @param memberUserId Member's user id.
     * @param todoListDTO Information of to-do list.
     * @return TodoListDTO object containing
     * @throws NoMemberFoundException when member with given id doesn't exists.
     */
    TodoListDTO createTodoList(long teamId, String memberUserId, TodoListDTO todoListDTO) throws NoTeamFoundException, NoMemberFoundException;

    /**
     * Update to-do list's information.
     * @param todoListId To-do list's id.
     * @param field Field to update.
     * @param value Updated value.
     * @return TodoListDTO object filled with updated to-do list.
     * @throws NoTodoListFoundException when to-do list with given id not found.
     */
    TodoListDTO updateTodoList(long todoListId, UpdatableTodoListInformation field, Object value) throws NoTodoListFoundException;

    /**
     * Delete to-do list of member.
     * @param todoListId To-do list's id.
     * @throws NoTodoListFoundException when to-do list with given id doesn't exists.
     */
    void deleteTodoList(long todoListId) throws NoTodoListFoundException;
}
