package com.study.day06intergration.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeTools {

    // 언제 이 도구를 부를지 AI 모델에게 알려주는 역할
    @Tool(description = "현재 날짜와 시간을 반환하는 도구")
    String getCurrentDateTime() {
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }
}
