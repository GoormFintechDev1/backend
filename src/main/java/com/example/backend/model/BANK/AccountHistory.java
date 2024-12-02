package com.example.backend.model.BANK;

import com.example.backend.model.enumSet.TransactionMeansEnum;
import com.example.backend.model.enumSet.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_history_id")
    private Long accountHistoryId;

    // 계좌ID
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account accountId;

    // 거래 타입 (매출(Revenue)/지출(Expense)
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionTypeEnum transactionType;

    // 거래 방식 (카드(Card) / 현금(Cash)
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_means")
    private TransactionMeansEnum transactionMeans;

    // 거래일
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    // 거래금액
    @Column(name = "amount", precision = 15, scale = 0)
    private BigDecimal amount;

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

    @Builder
    public AccountHistory(String transactionType, Account accountId, String transactionMeans, LocalDateTime transactionDate,BigDecimal amount, String category, String note, Boolean fixedExpenses, String storeName) {
        this.accountId = accountId;
        this.transactionType = TransactionTypeEnum.EXPENSE;
        this.transactionMeans = TransactionMeansEnum.CASH;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.fixedExpenses = fixedExpenses;
        this.storeName = storeName;
    }

}