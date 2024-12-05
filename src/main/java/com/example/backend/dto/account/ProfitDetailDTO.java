package com.example.backend.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProfitDetailDTO {

    // 순 이익
    private BigDecimal netProfit;
    // 총 수입
    private BigDecimal incomeTotal;
    // 매출 원가
    private BigDecimal saleCost;
    // 운영 비용
    private BigDecimal operatingExpense;
    // 세금
    private BigDecimal taxes;

}
