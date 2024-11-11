package com.example.backend.dto.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
@AllArgsConstructor
public class ExpenseGoalResponseDTO {

    private LocalDate goalMonth; // 목표 월
    private BigDecimal expenseGoal2Ago; // 지출 목표
    private BigDecimal monthlyExpense2Ago; // 실제 지출

    private BigDecimal expenseGoal1Ago; // 지출 목표
    private BigDecimal monthlyExpense1Ago; // 실제 지출

    private BigDecimal expenseGoal0Ago; // 지출 목표
    private BigDecimal monthlyExpense0Ago; // 실제 지출


}
