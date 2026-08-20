// package com.spring.ai.ai_agent_backend;

// import com.spring.ai.ai_agent_backend.tools.OrderTools;
// import lombok.RequiredArgsConstructor;
// import org.springframework.ai.chat.client.ChatClient;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// @RequiredArgsConstructor
// public class AIConfig {

// private final OrderTools orderTools;

// @Bean
// public ChatClient chatClient(ChatClient.Builder builder){
// return builder.defaultSystem("""
// You are a helpful assistant that helps user to get the status of their orders
// """)
// .defaultTools(orderTools)
// .build();
// }
// }
