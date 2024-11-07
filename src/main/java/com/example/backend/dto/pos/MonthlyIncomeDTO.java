package com.example.backend.dto.pos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class MonthlyIncomeDTO {


    private BigDecimal monthlyTotalncome; // 월 총 매출
    private BigDecimal monthlyCardIncome; // 월 카드 매출
    private BigDecimal monthlyCashIncome; // 월 현금 매출


}

