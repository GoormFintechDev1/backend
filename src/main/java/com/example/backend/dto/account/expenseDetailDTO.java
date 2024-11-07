package com.example.backend.dto.account;

import com.example.backend.model.enumSet.TransactionMeansEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class expenseDetailDTO {

    // 달별 지출 합계
    private BigDecimal totalMonthExpenses;

    // 카테고리별 이번 달 지출 합계 (카테고리명, 지출 금액)
    private Map<String, BigDecimal> categoryTotalExpenses;

    // 각 거래의 상세 정보
    private List<ExpenseDetail> expenseDetails;

    @Data
    @AllArgsConstructor
    public static class ExpenseDetail {
        // 거래일
        private LocalDateTime transactionDate;

        // 거래 방식 (카드, 현금)
        private TransactionMeansEnum transactionMeans;

        // 거래 금액
        private BigDecimal amount;

        // 고정 지출 여부
        private Boolean fixedExpenses;

        // 거래처
        private String storeName;

        // 카테고리
        private String category;

        // 노트
        private String note;
    }
}
