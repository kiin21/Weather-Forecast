package org.example.worldcast.controller;

import org.example.worldcast.domain.dto.request.ChatRequest;
import org.example.worldcast.service.impl.ChatServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatServiceImpl chatService;

    public ChatController(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest chatRequest) {
        return chatService.chat(chatRequest);
    }
}
