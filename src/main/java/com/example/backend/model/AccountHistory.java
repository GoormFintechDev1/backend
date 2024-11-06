package com.example.backend.model;

import com.example.backend.model.enumSet.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_history")
public class AccountHistory {

    // 기록 식별 ID
    @Id
    @Column(name = "history_id")
    private Long historyId;

    // 계좌ID
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account accountId;

    // 거래 타입 (매출(Revenue)/지출(Expense)
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionTypeEnum transactionType;

    // 거래일
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    // 거래금액
    @Column(name = "amount", precision = 15, scale = 0)
    private BigDecimal amount;

    // 거래 후 잔액
    @Column(name = "balance_after", precision = 15, scale = 0)
    private BigDecimal balanceAfter;

    // 카테고리 (공과금, 임대료, 재료비..)
    @Column(name = "category", length = 50)
    private String category;

    // 메모
    @Column(name = "note", nullable = true)
    private String note;

    // 고정지출 여부 / 고정지출 = true
    @Column(name = "fixed_expenses")
    private Boolean fixedExpenses;

    // 거래처
    @Column(name = "store_name", length = 50)
    private String storeName;

}