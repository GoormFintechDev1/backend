package com.example.backend.dto.account;

import com.example.backend.model.enumSet.TransactionMeansEnum;
import com.example.backend.model.enumSet.TransactionTypeEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateAccountHistoryDTO {
    // 거래 타입 (매출(Revenue)/지출(Expense)
    private TransactionTypeEnum transactionType;
    // 거래 방식 (카드(Card) / 현금(Cash)
    private TransactionMeansEnum transactionMeans;
    // 거래일
    private LocalDateTime transactionDate;
    // 거래 금액
    private BigDecimal amount;
    // 카테고리
    private String category;
    // 메모
    private String note;
    // 고정지출 여부 / 고정지출 = true
    private Boolean fixedExpenses;
    // 거래처
    private String storeName;
}
