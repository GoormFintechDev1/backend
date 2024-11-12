package com.example.backend.dto.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@AllArgsConstructor
public class GoalRequestDTO {
    private YearMonth goalMonth;
    private BigDecimal revenueGoal;
    private BigDecimal expenseGoal;
}
