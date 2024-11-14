package com.example.backend.dto.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@AllArgsConstructor
public class GoalResponseDTO {

    private YearMonth goalMonth; // 목표 월

    private BigDecimal revenueGoal0Ago; // 현재 달 매출 목표
    private BigDecimal monthlyRevenue0Ago; // 현재 달 실제 매출
    private BigDecimal expenseGoal0Ago; // 현재 달 지출 목표
    private BigDecimal monthlyExpense0Ago; // 현재 달 실제 지출
}
