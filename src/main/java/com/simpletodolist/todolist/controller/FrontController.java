package com.simpletodolist.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class FrontController {

    @GetMapping
    public String redirectToDocument() {
        return "redirect:/docs/index.html";
    }
}
