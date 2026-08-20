package com.spring.ai.ai_agent_backend.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final ChatClient chatClient;

    // Spring AI auto-configures ChatClient.Builder
    public GeminiController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * GET endpoint for quick browser testing
     * Example: http://localhost:8080/api/gemini/generate?message=Hello
     */
    @GetMapping("/generate")
    public Map<String, String> generate(
            @RequestParam(defaultValue = "Explain Spring Boot in 2 sentences") String message) {

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        return Map.of(
                "prompt", message,
                "response", response);
    }

    /**
     * POST endpoint for JSON requests
     */
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String prompt = request.getOrDefault("message", "Hello!");

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return Map.of("response", response);
    }
}
