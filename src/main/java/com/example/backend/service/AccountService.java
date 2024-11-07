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

    // 월별 지출 합계 구하는 함수
    private BigDecimal calculateTotalExpenses(Long accountId, YearMonth month) {
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
    private Map<String, BigDecimal> calculateCategoryWiseExpenses(Long accountId, YearMonth month) {
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

    // 오늘 지출 합계 구하는 함수 (오늘 날짜와 대조)
    private BigDecimal calculateTodayExpense(Long accountId) {
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
    private List<expenseDetailDTO.ExpenseDetail> getExpenseDetails(Long accountId, YearMonth month) {
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
    public expenseDTO showSimpleExpense(Long memberId,Long accountId, YearMonth month) {
        QAccount qAccount = QAccount.account;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;


        BigDecimal monthlyExpenses = calculateTotalExpenses(accountId, month);
        Map<String, BigDecimal> categoryExpenses = calculateCategoryWiseExpenses(accountId, month);
        BigDecimal todayExpense = calculateTodayExpense(accountId);

        // QueryDSL을 사용하여 memberId, accountId, 그리고 businessId가 연결된 관계인지 확인
        Boolean isAuthorized = queryFactory
                .selectOne()
                .from(qAccount)
                .join(qAccount.business, qBusinessRegistration)
                .where(
                        qBusinessRegistration.member.id.eq(memberId)
                                .and(qAccount.accountId.eq(accountId))
                )
                .fetchFirst() != null;

        if (!isAuthorized) {
            throw new BadRequestException("계좌 접근 권한이 없음.");
        }

        return new expenseDTO(monthlyExpenses, todayExpense != null ? todayExpense : BigDecimal.ZERO, categoryExpenses);
    }

    ////// 지출 상세 정보
    public expenseDetailDTO showDetailExpense(Long memberId,Long accountId, YearMonth month) {

        QAccount qAccount = QAccount.account;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;

        Map<String, BigDecimal> categoryTotalExpenses = calculateCategoryWiseExpenses(accountId, month);
        List<expenseDetailDTO.ExpenseDetail> expenseDetails = getExpenseDetails(accountId, month);
        BigDecimal monthlyExpenses = calculateTotalExpenses(accountId, month);

        // QueryDSL을 사용하여 memberId, accountId, 그리고 businessId가 연결된 관계인지 확인
        Boolean isAuthorized = queryFactory
                .selectOne()
                .from(qAccount)
                .join(qAccount.business, qBusinessRegistration)
                .where(
                        qBusinessRegistration.member.id.eq(memberId)
                                .and(qAccount.accountId.eq(accountId))
                )
                .fetchFirst() != null;

        if (!isAuthorized) {
            throw new BadRequestException("계좌 접근 권한이 없음.");
        }

        return new expenseDetailDTO(monthlyExpenses,categoryTotalExpenses, expenseDetails);
    }

}
