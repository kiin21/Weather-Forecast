package org.example.worldcast.service;

import org.example.worldcast.domain.dto.request.ChatRequest;

public interface ChatService {
    String chat(ChatRequest request);
}
