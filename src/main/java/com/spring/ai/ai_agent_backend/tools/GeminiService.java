package com.spring.ai.ai_agent_backend.tools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder chatClientBuilder, OrderTools orderTools, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are a helpful customer support assistant.
                        You help users with their orders (status checks, cancellations) using the provided tools,
                        AND you answer questions about the business using the provided context from uploaded documents.
                        If context from documents is provided, use it to answer the question accurately.
                        If no relevant context is found, say so honestly.
                        Use the provided tools whenever an order ID is mentioned.
                        """)
                .defaultTools(orderTools)
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(5)
                                        .similarityThreshold(0.5)
                                        .build())
                                .build()
                )
                .build();
    }

    public String generateText(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
