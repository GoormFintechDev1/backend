package com.example.backend.dto.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;


@Data
@AllArgsConstructor
public class GoalRequestDTO {
    private LocalDate goalMonth; // 목표 월
    private BigDecimal revenueGoal; // 매출 목표
    private BigDecimal expenseGoal; // 지출 목표
}
