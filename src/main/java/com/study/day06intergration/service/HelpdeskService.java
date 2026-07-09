package com.study.day06intergration.service;

import com.study.day06intergration.tool.CompanyRuleTools;
import com.study.day06intergration.tool.CustomerTools;
import com.study.day06intergration.tool.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class HelpdeskService {
    private final ChatClient chatClient;
    private final DateTimeTools dateTimeTools;
    private final CustomerTools customerTools;
    private final CompanyRuleTools companyRuleTools;

    public HelpdeskService(ChatClient.Builder builder,
                           @Qualifier("jdbcMemoryChatMemory") ChatMemory chatMemory,
                           DateTimeTools dateTimeTools,
                           CustomerTools customerTools,
                           CompanyRuleTools companyRuleTools) {
        this.chatClient = builder
                .defaultSystem("""
                        당신은 사내 헬프데스크 AI 어시스턴트입니다.
                        고객 문의에 답할 때는 필요하면 도구로 고객 등급·사내 규칙·현재 시각을 확인하세요.
                        고객 등급(VIP/일반/신규)에 따라 응대 우선순위와 톤을 맞추세요.
                        정중하고 간결한 한국어로 답변하세요.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor()
                )
                .build();
        this.dateTimeTools = dateTimeTools;
        this.customerTools = customerTools;
        this.companyRuleTools = companyRuleTools;
    }

        public String chat(String question, String conversationId) {
            return chatClient.prompt()
                    .user(question)
                    .tools(dateTimeTools, customerTools, companyRuleTools) // 도구
                    .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId)) // 기억
                    .call()
                    .content();
        }

    // call -> stream
    public Flux<String> chatStream(String question, String conversationId) {
        return chatClient.prompt()
                .user(question)
                .tools(dateTimeTools, customerTools, companyRuleTools) // 도구
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId)) // 기억
                .stream()   // call -> stream
                .content();
    }

}
