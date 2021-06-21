package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.TodoDTO;
import com.simpletodolist.todolist.domain.dto.TodoListDTO;
import com.simpletodolist.todolist.domain.dto.TodoListsDTO;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.todo.TodoService;
import com.simpletodolist.todolist.service.todolist.TodoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final TodoService todoService;
    private final TodoListService todoListService;
    private final MemberService memberService;


    /**
     * Get information of user based on given user id.
     * @param memberId User's user id.
     * @return 200 OK with body filled with user information.
     */
    @GetMapping("/{memberUserId}")
    public ResponseEntity<MemberDTO> memberInfo(@PathVariable(name = "memberUserId") String memberId){
        return ResponseEntity.ok(memberService.getMemberDetails(memberId));
    }


    /**
     * Delete user.
     * @param memberId User's user id.
     */
    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public void withdrawMember(@PathVariable(name = "memberId") String memberId) {
        memberService.withdrawMember(memberId);
    }


    /**
     * Get to-do lists of member based on given user id.
     * @param memberId User's user id.
     * @return 200 OK with body filled with to-do lists.
     */
    @GetMapping("/{memberId}/todos")
    public ResponseEntity<TodoListsDTO> getTodoListsOfMember(@PathVariable(name = "memberId") String memberId){
        return ResponseEntity.ok(todoListService.readTodoListsOfMember(memberId));
    }


    /**
     * Get todos of member's to-do list.
     * @param memberId Member's user id.
     * @param todoListId To-do list's id.
     * @return TodoListDTO containing to-do list's information.
     */
    @GetMapping("/{memberId}/todos/{todoListId}")
    public ResponseEntity<TodoListDTO> getTodoList(@PathVariable(name = "memberId") String memberId,
                                                   @PathVariable(name = "todoListId") long todoListId) {

        return ResponseEntity.ok(todoListService.getTodoListDetail(memberId, todoListId));
    }


    /**
     * Create new todolist of member.
     * @param memberId Member id to create to-do list.
     * @param todoListDTO Information of creating to- do list.
     * @return TodoListDTO filled with created to-do list.
     */
    @PostMapping("/{memberId}/todos")
    public ResponseEntity<TodoListDTO> createTodoList(@PathVariable(name = "memberId") String memberId,
                                                      @Valid @RequestBody TodoListDTO todoListDTO) {
        return ResponseEntity.ok(todoListService.createTodoList(memberId, todoListDTO));
    }


    /**
     * Delete to-do list.
     * @param memberId To-do list's owner user id.
     * @param todoListId Id of deleting to-do list.
     */
    @DeleteMapping("/{memberId}/todos/{todoListId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTodoList(@PathVariable(name = "memberId") String memberId,
                               @PathVariable(name = "todoListId") long todoListId) {
        todoListService.deleteTodoList(memberId, todoListId);
    }


    /**
     * Read single to-do.
     * @param memberId Member's user id.
     * @param todoListId To-do list's id.
     * @param todoId To-do's id.
     * @return TodoDTO object filled with to-do.
     */
    @GetMapping("/{memberId}/todos/{todoListId}/{todoId}")
    public ResponseEntity<TodoDTO> readTodo(@PathVariable(name = "memberId") String memberId,
                                            @PathVariable(name = "todoListId") long todoListId,
                                            @PathVariable(name = "todoId") long todoId) {
        return ResponseEntity.ok(todoService.readTodo(memberId, todoListId, todoId));
    }


    /**
     * Write new to-do into to-do list.
     * @param memberId To-do writer's user id
     * @param todoListId Writer's to-do list id.
     * @param todoDTO To-do content.
     * @return 200 OK with body filled with written to-do.
     */
    @PostMapping("/{memberId}/todos/{todoListId}")
    public ResponseEntity<TodoDTO> writeTodo(@PathVariable(name = "memberId") String memberId,
                                             @PathVariable(name = "todoListId") long todoListId,
                                             @Valid @RequestBody TodoDTO todoDTO){
        return ResponseEntity.ok(todoService.writeTodo(memberId, todoListId, todoDTO));
    }


    /**
     * Delete to-do.
     * @param memberId Member's user id.
     * @param todoListId To-do list's id.
     * @param todoId To-do's id.
     */
    @DeleteMapping("/{memberId}/todos/{todoListId}/{todoId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTodo(@PathVariable(name = "memberId") String memberId,
                           @PathVariable(name = "todoListId") long todoListId,
                           @PathVariable(name = "todoId") long todoId) {
        todoService.deleteTodo(memberId, todoListId, todoId);
    }
}
