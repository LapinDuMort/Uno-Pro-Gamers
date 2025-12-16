package com.progamers.uno.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String index() {
        return "home/index";
    }

    @GetMapping("/lobby")
    public String lobby() { return "lobby/index"; }
}
