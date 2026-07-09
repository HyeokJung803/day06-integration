package com.study.day06intergration.service;

import com.study.day06intergration.dto.HistoryMessage;
import com.study.day06intergration.entity.ChatHistoryEntity;
import com.study.day06intergration.repository.ChatHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    public ChatHistoryService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    // 채팅 이력을 가져오는 로직
    public List<HistoryMessage> history(String conversationId) {
        return chatHistoryRepository.findByConversationIdOrderByIdAsc(conversationId).stream()
                .map(e -> new HistoryMessage(e.getRole(), e.getContent()))
                .toList();
    }

    // 채팅 이력을 저장하는 로직
    public void save(String conversationId, String role, String content) {
        chatHistoryRepository.save(new ChatHistoryEntity(conversationId, role, content, Instant.now()));
    }

}
