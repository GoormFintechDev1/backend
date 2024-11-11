package com.example.backend.dto.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@AllArgsConstructor
public class ExpenseGoalRequestDTO {
    private LocalDate goalMonth; // 목표 월
    private BigDecimal expenseGoal; // 지출 목표
}
