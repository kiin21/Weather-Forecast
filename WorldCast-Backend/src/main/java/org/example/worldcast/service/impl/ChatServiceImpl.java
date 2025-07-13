package org.example.worldcast.service.impl;

import org.example.worldcast.domain.dto.request.ChatRequest;
import org.example.worldcast.service.ChatService;
import org.example.worldcast.service.GenAIService;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    private final GenAIService aiService;

    public ChatServiceImpl(GenAIService aiService) {
        this.aiService = aiService;
    }

    @Override
    public String chat(ChatRequest request) {
        return aiService.chatWithAI(request.message());
    }
}
