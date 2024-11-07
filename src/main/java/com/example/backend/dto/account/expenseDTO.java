package com.example.backend.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class expenseDTO {

    // 달별 지출 합계
    private BigDecimal totalMonthExpenses;

    // 오늘 지출 합계
    private BigDecimal totalTodayExpense;

    // 카테고리별 이번 달 지출 합계 (카테고리명, 지출 금액)
    private Map<String, BigDecimal> categoryExpenses;
}
