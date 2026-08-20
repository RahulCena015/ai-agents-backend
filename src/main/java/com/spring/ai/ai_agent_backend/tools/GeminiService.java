package com.spring.ai.ai_agent_backend.tools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder chatClientBuilder, OrderTools orderTools) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are a helpful customer support assistant that helps users get the status of their orders or cancel orders.
                        Use the provided tools whenever an order ID is mentioned.
                        """)
                .defaultTools(orderTools)
                .build();
    }

    public String generateText(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
