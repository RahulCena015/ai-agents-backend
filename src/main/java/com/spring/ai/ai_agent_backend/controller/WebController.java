package com.spring.ai.ai_agent_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String chat() {
        return "chat";
    }
}
