package org.example.worldcast.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.worldcast.domain.dto.request.ChatRequest;
import org.example.worldcast.service.GenAIService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final GenAIService genAIService;

    public ChatController(GenAIService genAIService) {
        this.genAIService = genAIService;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest chatRequest) {
        return genAIService.chatWithAI(chatRequest.message());
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody ChatRequest chatRequest) {
        return genAIService
                .chatWithAIStream(chatRequest.message())
                .filter(token -> !token.isEmpty())
                .map(String::trim)
                .doOnNext(token -> log.info("Token: {}", token));
    }
}
