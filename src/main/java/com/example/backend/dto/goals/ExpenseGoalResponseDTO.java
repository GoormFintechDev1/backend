package com.example.backend.dto.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExpenseGoalResponseDTO {
    private LocalDate goalMonth; // 목표 월
    private BigDecimal expenseGoal; // 지출 목표
    private BigDecimal monthlyExpense; // 실제 지출
    private Boolean expenseAchieved; // 지출 달성 여부

}
