package com.example.backend.dto.pos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class IncomeHistoryDTO {

    private BigDecimal totalIncome2Ago; // 2개월 전 총 매출
    private BigDecimal totalIncome1Ago; // 1개월 전 총 매출
    private BigDecimal totalIncome0Ago; // 이번 달 총 매출

}
