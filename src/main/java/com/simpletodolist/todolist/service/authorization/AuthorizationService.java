package com.simpletodolist.todolist.service.authorization;

import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.team.NotJoinedTeamException;
import com.simpletodolist.todolist.exception.team.NotTeamLeaderException;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.exception.todo.NotWriterTodoException;
import com.simpletodolist.todolist.exception.todolist.LockedTodoListException;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import com.simpletodolist.todolist.exception.todolist.NotTodoListOwnerException;

public interface AuthorizationService {

    /**
     * Check if member joined team.
     * @param memberUserId Member's user id.
     * @param teamId Team's id.
     * @throws NoMemberFoundException when member with given id not exists.
     * @throws NoTeamFoundException when team with given id not exists.
     * @throws NotJoinedTeamException when member is not joined.
     */
    void authorizeTeamMember(String memberUserId, long teamId)
            throws NoMemberFoundException, NoTeamFoundException, NotJoinedTeamException;

    /**
     * Check if member is leader of this team.
     * @param memberUserId Member's user id.
     * @param teamId Team's id.
     * @throws NoMemberFoundException when member with given id not exists.
     * @throws NoTeamFoundException when team with given id not exists.
     * @throws NotJoinedTeamException when member is not joined team.
     * @throws NotTeamLeaderException when member is not the leader of this team.
     */
    void authorizeTeamLeader(String memberUserId, long teamId)
            throws NoMemberFoundException, NoTeamFoundException, NotJoinedTeamException, NotTeamLeaderException;

    /**
     * Authorize member to to-do list.
     * @param memberUserId Member's user id.
     * @param todoListId To-do list's id.
     * @throws NoMemberFoundException when member with given id not exists.
     * @throws NoTodoListFoundException when to-do list with given id not exists.
     * @throws NotTodoListOwnerException when member is not owner of to-do list.
     */
    void authorizeTodoListOwner(String memberUserId, long todoListId) throws NoMemberFoundException, NoTodoListFoundException, NotTodoListOwnerException;

    /**
     * Authorize member to to-do.
     * @param memberUserId Member's user id.
     * @param todoId To-do's id.
     * @throws NoMemberFoundException when member with given id not found.
     * @throws NoTodoFoundException when to-do with given id not found.
     * @throws NotWriterTodoException when to-do is locked and member is not writer.
     */
    void authorizeTodoWriter(String memberUserId, long todoId) throws NoMemberFoundException, NoTodoFoundException, NotWriterTodoException;

    /**
     * Check if team contains to-do list.
     * @param teamId Team's id.
     * @param todoListId To-do list's id.
     * @throws NoTeamFoundException when team with given id not exists.
     * @throws NoTodoListFoundException when to-do list with given id not exists or not in team.
     */
    void validateTeamContainsTodoList(long teamId, long todoListId) throws NoTeamFoundException, NoTodoListFoundException;

    /**
     * Check if to-do list contains to-do.
     * @param todoListId To-do list's id.
     * @param todoId To-do's id.
     * @throws NoTodoListFoundException when to-do list with given id not found.
     * @throws NoTodoFoundException when to-do with given id not found.
     */
    void validateTodoListContainsTodo(long todoListId, long todoId) throws NoTodoListFoundException, NoTodoFoundException;

    /**
     * Checks if member have full authorization about team.
     * @param memberUserId Member's user id.
     * @param teamId Team's id.
     * @throws NoMemberFoundException when member with given id not found.
     * @throws NoTeamFoundException when team with given id not found.
     * @throws NotJoinedTeamException when member is not joined this team.
     * @throws NotTeamLeaderException when member is not leader of this team.
     */
    void fullAuthorization(String memberUserId, long teamId)
        throws NoMemberFoundException, NoTeamFoundException, NotJoinedTeamException, NotTeamLeaderException;

    /**
     * Checks if member joined team and can access to-do list.
     * If to-do list is locked, member should be owner of resource.
     * If member is leader of this team then skip authorization of to-do list.
     * @param memberUserId member's user id.
     * @param teamId team's id.
     * @param todoListId to-do list's id.
     * @throws NoMemberFoundException when member with given id not found.
     * @throws NoTeamFoundException when team with given id not found.
     * @throws NoTodoListFoundException when to-do list with given id not found.
     * @throws NotJoinedTeamException when member is not joined team.
     * @throws NotTodoListOwnerException when to-do list is locked and member is not owner.
     */
    void fullAuthorization(String memberUserId, long teamId, long todoListId)
        throws NoMemberFoundException, NoTeamFoundException, NoTodoListFoundException,
            NotJoinedTeamException, NotTodoListOwnerException;

    /**
     * Checks if member joined team and can access to-do list and to-do.
     * If to-do list or to-do is locked, member should be owner(or writer) of resource.
     * If member is leader of this team then skip authorization of to-do list and to-do.
     * @param memberUserId member's user id.
     * @param teamId team's id.
     * @param todoListId to-do list's id.
     * @param todoId to-do's id.
     * @throws NoMemberFoundException when member with given id not found.
     * @throws NoTeamFoundException when team with given id not found.
     * @throws NoTodoListFoundException when to-do list with given id not found.
     * @throws NoTodoFoundException when to-do with given id not found.
     * @throws NotJoinedTeamException when member is not joined team.
     * @throws LockedTodoListException when to-do list is locked and member is not owner.
     * @throws NotWriterTodoException when to-do is locked and member is not writer.
     */
    void fullAuthorization(String memberUserId, long teamId, long todoListId, long todoId)
            throws NoMemberFoundException, NoTeamFoundException, NoTodoListFoundException, NoTodoFoundException,
            NotJoinedTeamException, LockedTodoListException, NotWriterTodoException;


}