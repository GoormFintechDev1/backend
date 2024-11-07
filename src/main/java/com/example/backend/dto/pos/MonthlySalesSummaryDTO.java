package com.example.backend.dto.pos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class MonthlySalesSummaryDTO {


    private BigDecimal monthlyTotalncome; // 월 매출 총합
    private List<DailyIncomeDTO> dailyIncomeList; // 일별 매출 리스트

}

