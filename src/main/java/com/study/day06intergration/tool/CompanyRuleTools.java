package com.study.day06intergration.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CompanyRuleTools {

    private static final Map<String, String> RULES = Map.of(
            "배포", "배포는 화·목 오후에만 가능하며 금요일 배포는 금지입니다.",
            "코드리뷰", "모든 변경은 Pull Request로 올리고 최소 1인의 리뷰 승인 후 병합합니다.",
            "근무", "코어타임은 10:00~16:00이며 재택근무는 주 2회까지 가능합니다.",
            "보안", "API 키·비밀번호는 코드에 하드코딩하지 않고 환경변수로 관리합니다.");

    @Tool(description = "주제 키워드가 있으면, 사내 개발팀 규칙 조회한다.")
    String getCompanyRule(
            @ToolParam(description = "사내 규칙 주제. 주제는 배포, 코드리뷰, 근무, 보안")  String topic
    ) {
        return RULES.getOrDefault(topic, "해당 주제의 규칙은 등록되어 있지 않습니다.");
    }

}
