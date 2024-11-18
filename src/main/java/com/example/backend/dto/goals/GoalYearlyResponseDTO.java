package com.example.backend.dto.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@AllArgsConstructor
public class GoalYearlyResponseDTO {

    private int goalMonth; // 목표 월

    private BigDecimal revenueGoal; // 현재 달 매출 목표
    private BigDecimal realRevenue; // 현재 달 실제 매출
    private BigDecimal expenseGoal; // 현재 달 지출 목표
    private BigDecimal realExpense; // 현재 달 실제 지출
}
