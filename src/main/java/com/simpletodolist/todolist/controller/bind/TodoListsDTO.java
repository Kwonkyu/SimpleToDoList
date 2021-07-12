package com.simpletodolist.todolist.controller.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.TodoList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TodoListsDTO {

    @JsonProperty("todoLists")
    private List<TodoListDTO> todoLists = new ArrayList<>();

    public TodoListsDTO(List<TodoList> todoLists){
        this.todoLists = todoLists.stream().map(TodoListDTO::new).collect(Collectors.toList());
    }
}
