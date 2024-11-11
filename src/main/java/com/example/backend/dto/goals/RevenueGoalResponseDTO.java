package com.example.backend.dto.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
@AllArgsConstructor
public class RevenueGoalResponseDTO {
    private YearMonth goalMonth; // 목표 월
    private BigDecimal revenueGoal; // 매출 목표
    private BigDecimal monthlyRevenue; // 실제 매출
    private Boolean revenueAchieved; // 매출 달성 여부

}
