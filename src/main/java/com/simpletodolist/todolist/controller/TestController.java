package com.simpletodolist.todolist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/secret")
@RestController
public class TestController {

    @GetMapping
    public String doNone() {
        return "HELO!";
    }
}
