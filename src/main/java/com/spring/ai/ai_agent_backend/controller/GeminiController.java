package com.spring.ai.ai_agent_backend.controller;

import com.spring.ai.ai_agent_backend.tools.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    /**
     * GET endpoint for quick browser testing
     * Example: http://localhost:8081/api/gemini/generate?message=What is the status of order 1042?
     */
    @GetMapping("/generate")
    public Map<String, String> generate(
            @RequestParam(defaultValue = "What is the status of order 1042?") String message) {

        String response = geminiService.generateText(message);

        return Map.of(
                "prompt", message,
                "response", response != null ? response : "");
    }

    /**
     * POST endpoint for JSON requests
     */
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String prompt = request.getOrDefault("message", "What is the status of order 1042?");

        String response = geminiService.generateText(prompt);

        return Map.of("response", response != null ? response : "");
    }
}
