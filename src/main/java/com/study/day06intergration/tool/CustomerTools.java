package com.study.day06intergration.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomerTools {

    public record CustomerGrade(String customerId, String name, String grade, String responseSla) {}

    // DB에 조회 가정
    private static final Map<String, CustomerGrade> CUSTOMERS = Map.of(
            "C001", new CustomerGrade("C001", "김에이스", "VIP", "1시간 이내"),
            "C002", new CustomerGrade("C002", "이보람", "일반", "1영업일 이내"),
            "C003", new CustomerGrade("C003", "박신입", "신규", "1영업일 이내"));

    @Tool(description = "고객 ID로 고객의 등급 정보를 조회하는 도구")
    CustomerGrade getCustomerGrade(
            @ToolParam(description = "고객 ID, ex: C001 형식으로 생김.") String customerId) {
        CustomerGrade grade = CUSTOMERS.get(customerId);  // DB에서 ID기반으로 조회
        if (grade == null) {
            return new CustomerGrade(customerId, "미등록자", "미확인", "미확인");
        }
        return grade;
    }
}

