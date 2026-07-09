package com.study.day06intergration.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String ask(String question) {
        return chatClient.prompt().user(question).call().content();
    }

    // stream으로 반환
    public Flux<String> askStream(String question) {
        return chatClient.prompt().
                user(question).stream().content();
    }

}
