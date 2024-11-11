package com.example.backend.dto.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RevenueGoalResponseDTO {
    private LocalDate goalMonth; // 목표 월
    private BigDecimal revenueGoal2Ago; // 매출 목표
    private BigDecimal monthlyRevenue2Ago; // 실제 매출

    private BigDecimal revenueGoal1Ago; // 매출 목표
    private BigDecimal monthlyRevenue1Ago; // 실제 매출


    private BigDecimal revenueGoal0Ago; // 매출 목표
    private BigDecimal monthlyRevenue0Ago; // 실제 매출


}
