package com.study.day06intergration.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="chat_history")
public class ChatHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String conversationId;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false, columnDefinition = "text")
    private String content;
    @Column(nullable = false)
    private Instant createdAt;

    public ChatHistoryEntity() {
    }

    public ChatHistoryEntity(String conversationId, String role, String content, Instant createdAt) {
        this.conversationId = conversationId;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
