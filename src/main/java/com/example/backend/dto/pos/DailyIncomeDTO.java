package com.example.backend.dto.pos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

@Data
@AllArgsConstructor
public class DailyIncomeDTO {

    private LocalDate date; // 특정 일자
    private BigDecimal totalIncome; // 해당 일자 총 매출
    private BigDecimal cardIncome; // 해당 일자 카드 매출
    private BigDecimal cashIncome; // 해당 일자 현금 매출
}
