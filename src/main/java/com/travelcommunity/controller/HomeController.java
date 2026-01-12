package com.travelcommunity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Travel Community API! The server is running. Use /api/auth/login to authenticate.";
    }
}
