package com.simpletodolist.todolist.controller.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Todo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TodosDTO {

    @JsonProperty("todos")
    List<TodoDTO> todos = new ArrayList<>();

    public TodosDTO(List<Todo> todos){
        this.todos = todos.stream().map(TodoDTO::new).collect(Collectors.toList());
    }
}
