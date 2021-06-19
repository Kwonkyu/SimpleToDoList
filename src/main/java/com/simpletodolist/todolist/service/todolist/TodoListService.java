package com.simpletodolist.todolist.service.todolist;

import com.simpletodolist.todolist.domain.dto.TodoListDTO;
import com.simpletodolist.todolist.domain.dto.TodoListsDTO;
import com.simpletodolist.todolist.exception.NoMemberFoundException;
import com.simpletodolist.todolist.exception.NoTodoListFoundException;

public interface TodoListService {

    /**
     * Get information of to-do list.
     * @param memberUserId Member's user id.
     * @param todoListId To-do list's id.
     * @return TodoListDTO object containing information of to-do list.
     * @throws NoMemberFoundException when member does not exists.
     * @throws NoTodoListFoundException when to-do list does not exists.
     */
    TodoListDTO getTodoListDetail(String memberUserId, long todoListId) throws NoMemberFoundException, NoTodoListFoundException;

    /**
     * Get to-do lists of member.
     * @param memberId Member's user id.
     * @return TodoListsDTO object containing to-do lists of member.
     * @throws NoMemberFoundException when member of given id does not exists.
     */
    TodoListsDTO readTodoListsOfMember(String memberId) throws NoMemberFoundException;

    /**
     * Create to-do list of member.
     * @param memberUserId Member's user id.
     * @param todoListDTO Information of to-do list.
     * @return TodoListDTO object containing
     * @throws NoMemberFoundException when member with given id doesn't exists.
     */
    TodoListDTO createTodoList(String memberUserId, TodoListDTO todoListDTO) throws NoMemberFoundException;

    /**
     * Delete to-do list of member.
     * @param memberUserId Member's user id.
     * @param todoListId To-do list's id.
     * @throws NoMemberFoundException when member with given id doesn't exists.
     * @throws NoTodoListFoundException when to-do list with given id doesn't exists.
     */
    void deleteTodoList(String memberUserId, long todoListId) throws NoMemberFoundException, NoTodoListFoundException;
}
