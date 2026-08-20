// package com.spring.ai.ai_agent_backend.controller;

// import com.spring.ai.ai_agent_backend.service.ChatService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/chat")
// @RequiredArgsConstructor
// public class ChatController {

// private final ChatService chatService;

// @GetMapping("/query")
// public ResponseEntity<String> chat(@RequestBody String query){
// return ResponseEntity.ok(chatService.chat(query));
// }
// }
