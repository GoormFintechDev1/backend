package com.example.backend.service;

import com.example.backend.dto.account.expenseDTO;
import com.example.backend.dto.account.expenseDetailDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.*;
import com.example.backend.model.enumSet.TransactionTypeEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final JPAQueryFactory queryFactory;

    // 로그인한 유저의 accountId를 가져오는 로직
    private Long getAccountIdByMemberId(Long memberId) {
        QAccount qAccount = QAccount.account;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;

        Long accountId = queryFactory
                .select(qAccount.accountId)
                .from(qAccount)
                .join(qAccount.business, qBusinessRegistration)
                .where(qBusinessRegistration.member.id.eq(memberId))
                .fetchOne();

        if (accountId == null) {
            throw new BadRequestException("해당 사용자는 계좌가 없습니다.");
        }
        return accountId;
    }

    // 월별 지출 합계 구하는 함수
    private BigDecimal calculateTotalExpenses(YearMonth month, Long memberId) {
        Long accountId = getAccountIdByMemberId(memberId);

        QAccountHistory accountHistory = QAccountHistory.accountHistory;

        return queryFactory
                .select(accountHistory.amount.sum())
                .from(accountHistory)
                .where(accountHistory.accountId.accountId.eq(accountId)
                        .and(accountHistory.transactionType.eq(TransactionTypeEnum.EXPENSE))
                        .and(accountHistory.transactionDate.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();
    }

    // 월별 카테고리별 지출 합계 구하는 함수
    private Map<String, BigDecimal> calculateCategoryWiseExpenses(YearMonth month, Long memberId) {
        Long accountId = getAccountIdByMemberId(memberId);

        QAccountHistory accountHistory = QAccountHistory.accountHistory;

        return queryFactory
                .from(accountHistory)
                .select(accountHistory.category, accountHistory.amount.sum())
                .where(accountHistory.accountId.accountId.eq(accountId)
                        .and(accountHistory.transactionType.eq(TransactionTypeEnum.EXPENSE))
                        .and(accountHistory.transactionDate.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .groupBy(accountHistory.category)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(accountHistory.category),
                        tuple -> tuple.get(accountHistory.amount.sum())
                ));
    }

    // 오늘 지출 합계 구하는 함수
    private BigDecimal calculateTodayExpense(Long memberId) {
        Long accountId = getAccountIdByMemberId(memberId);
        QAccountHistory accountHistory = QAccountHistory.accountHistory;
        LocalDate today = LocalDate.now();

        return queryFactory
                .select(accountHistory.amount.sum())
                .from(accountHistory)
                .where(accountHistory.accountId.accountId.eq(accountId)
                        .and(accountHistory.transactionType.eq(TransactionTypeEnum.EXPENSE))
                        .and(accountHistory.transactionDate.year().eq(today.getYear()))
                        .and(accountHistory.transactionDate.month().eq(today.getMonthValue()))
                        .and(accountHistory.transactionDate.dayOfMonth().eq(today.getDayOfMonth())))
                .fetchOne();
    }

    // 월별 상세 지출 정보 가져오는 함수
    private List<expenseDetailDTO.ExpenseDetail> getExpenseDetails(YearMonth month, Long memberId) {
        Long accountId = getAccountIdByMemberId(memberId);
        QAccountHistory accountHistory = QAccountHistory.accountHistory;

        return queryFactory
                .selectFrom(accountHistory)
                .where(accountHistory.accountId.accountId.eq(accountId)
                        .and(accountHistory.transactionType.eq(TransactionTypeEnum.EXPENSE))
                        .and(accountHistory.transactionDate.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .orderBy(accountHistory.transactionDate.desc())
                .fetch()
                .stream()
                .map(record -> new expenseDetailDTO.ExpenseDetail(
                        record.getTransactionDate(),
                        record.getTransactionMeans(),
                        record.getAmount(),
                        record.getFixedExpenses(),
                        record.getStoreName(),
                        record.getCategory(),
                        record.getNote()
                ))
                .collect(Collectors.toList());
    }

    ///////// 지출 요약
    public expenseDTO showSimpleExpense(Long memberId, YearMonth month) {
        BigDecimal monthlyExpenses = calculateTotalExpenses(month, memberId);
        Map<String, BigDecimal> categoryExpenses = calculateCategoryWiseExpenses(month, memberId);
        BigDecimal todayExpense = calculateTodayExpense(memberId);

        return new expenseDTO(
                monthlyExpenses,
                todayExpense != null ? todayExpense : BigDecimal.ZERO,
                categoryExpenses
        );
    }

    ////// 지출 상세 정보
    public expenseDetailDTO showDetailExpense(Long memberId, YearMonth month) {
        BigDecimal monthlyExpenses = calculateTotalExpenses(month, memberId);
        Map<String, BigDecimal> categoryTotalExpenses = calculateCategoryWiseExpenses(month, memberId);
        List<expenseDetailDTO.ExpenseDetail> expenseDetails = getExpenseDetails(month, memberId);

        return new expenseDetailDTO(
                monthlyExpenses,
                categoryTotalExpenses,
                expenseDetails
        );
    }
}
