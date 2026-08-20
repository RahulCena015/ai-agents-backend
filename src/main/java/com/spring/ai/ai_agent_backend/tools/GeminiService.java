package com.spring.ai.ai_agent_backend.tools;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;
    private final String model;

    public GeminiService(
            @Value("${spring.ai.google.genai.api-key:${gemini.api-key:}}") String apiKey,
            @Value("${spring.ai.google.genai.chat.options.model:${gemini.model:gemini-3.6-flash}}") String model) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.model = model;
    }

    public String generateText(String prompt) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    this.model,
                    prompt,
                    null);
            return response != null ? response.text() : null;
        } catch (Exception e) {
            throw new RuntimeException("Error calling Gemini API", e);
        }
    }
}
