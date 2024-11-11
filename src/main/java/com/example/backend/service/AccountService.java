package com.example.backend.service;

import com.example.backend.dto.account.expenseDTO;
import com.example.backend.dto.account.expenseDetailDTO;
import com.example.backend.dto.account.expenseWeekDTO;
import com.example.backend.dto.account.profitDetailDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.*;
import com.example.backend.model.enumSet.TransactionTypeEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
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
    // 로그인한 유저의 posId를 가져오는 로직
    private Long getPosIdByMemberId(Long memberId) {
        QPos qPos = QPos.pos;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;

        Long posId = queryFactory
                .select(qPos.posId)
                .from(qPos)
                .join(qPos.businessRegistration, qBusinessRegistration)
                .where(qBusinessRegistration.member.id.eq(memberId))
                .fetchOne();

        if (posId == null) {
            throw new BadRequestException("해당 사용자는 포스가 없습니다.");
        }
        return posId;
    }


    // 월별 지출 합계 구하는 함수
    public BigDecimal calculateTotalExpenses(YearMonth month, Long memberId) {
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
    // 월별 총수익 합계 구하는 함수
    private BigDecimal calculateTotalRevenue(YearMonth month, Long memberId) {
        Long accountId = getAccountIdByMemberId(memberId);
        QAccountHistory accountHistory = QAccountHistory.accountHistory;

        return queryFactory
                .select(accountHistory.amount.sum())
                .from(accountHistory)
                .where(accountHistory.accountId.accountId.eq(accountId)
                        .and(accountHistory.transactionType.eq(TransactionTypeEnum.REVENUE))
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

    ////// 순 이익 (총수익 - 총지출)
    public BigDecimal showNetProfit(Long memberId, YearMonth month) {
        // 총수익 계산
        BigDecimal totalRevenue = calculateTotalRevenue(month, memberId);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        // 총지출 계산
        BigDecimal totalExpenses = calculateTotalExpenses(month, memberId);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        // 순이익 = 총수익 - 총지출
        return totalRevenue.subtract(totalExpenses);
    }

    /////// 순이익 상세
    public profitDetailDTO showProfitDetail(Long memberId, YearMonth month) {
        Long accountId = getAccountIdByMemberId(memberId);
        Long posId = getPosIdByMemberId(memberId);
        QAccountHistory accountHistory = QAccountHistory.accountHistory;
        QPosSales posSales= QPosSales.posSales;
        QPos pos= QPos.pos;

        // 순 이익
        BigDecimal netProfit= showNetProfit(memberId,month);
        // 총 수입
        BigDecimal incomeTotal= calculateTotalRevenue(month, memberId);

        // 매출 원가 (지출에서 카테고리가 '재료비', '인건비,', '물류비')
        BigDecimal saleCost = queryFactory
                .select(accountHistory.amount.sum())
                .from(accountHistory)
                .where(accountHistory.accountId.accountId.eq(accountId)
                    .and(accountHistory.transactionType.eq(TransactionTypeEnum.EXPENSE))
                    .and(accountHistory.category.in("재료비", "인건비", "물류비")))
                .fetchOne();

        // 운영 비용 (지출에서 카테고리가 '임대료', '통신비', '유지보수비', '공과금')
        BigDecimal operatingExpense= queryFactory
                .select(accountHistory.amount.sum())
                .from(accountHistory)
                .where(accountHistory.accountId.accountId.eq(accountId)
                        .and(accountHistory.transactionType.eq(TransactionTypeEnum.EXPENSE))
                        .and(accountHistory.category.in("임대료", "통신비", "유지보수비","공과금")))
                .fetchOne();


        // 세금 (지출에서 카테고리가 '세금', 매출의 '부가세' vat_amount)
        // 1. accountHistory에서 '세금' 항목의 합계 구하기
        BigDecimal accountTaxes = queryFactory
                .select(accountHistory.amount.sum())
                .from(accountHistory)
                .where(accountHistory.accountId.accountId.eq(accountId)
                        .and(accountHistory.transactionType.eq(TransactionTypeEnum.EXPENSE))
                        .and(accountHistory.category.eq("세금")))
                .fetchOne();
        // 2. posSales에서 vatAmount 항목의 합계 구하기
        BigDecimal posVatAmount = queryFactory
                .select(posSales.vatAmount.sum())
                .from(posSales)
                .join(posSales.pos, pos)
                .where(pos.posId.eq(posSales.pos.posId)
                        .and(pos.account.accountId.eq(accountId)))
                .fetchOne();
        // 합산 (null 은 0으로 )
        if (accountTaxes == null) accountTaxes = BigDecimal.ZERO;
        if (posVatAmount == null) posVatAmount = BigDecimal.ZERO;

        // 3. 두 항목을 더하여 최종 세금 계산
        BigDecimal taxes = accountTaxes.add(posVatAmount);


        return new profitDetailDTO(
                netProfit,
                incomeTotal,
                saleCost,
                operatingExpense,
                taxes
        );
    }

    /// 주차를 계산하는 함수 (json 형식으로 값 반환)
    public List<Map<String, String>> calculateWeeksInMonth(YearMonth month) {
        List<Map<String, String>> weeks = new ArrayList<>();

        // 해당 월의 첫날과 마지막 날 계산
        LocalDate firstDay = LocalDate.of(month.getYear(), month.getMonth(), 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDate current = firstDay;

        while (current.isBefore(lastDay) || current.equals(lastDay)) {
            // 주차별 시작일과 종료일 계산
            LocalDate startOfWeek = current.with(java.time.DayOfWeek.MONDAY);
            LocalDate endOfWeek = current.with(java.time.DayOfWeek.SUNDAY);

            // 해당 월에 속하는 날짜만 포함
            if (startOfWeek.isBefore(firstDay)) startOfWeek = firstDay;
            if (endOfWeek.isAfter(lastDay)) endOfWeek = lastDay;

            Map<String, String> weekInfo = new HashMap<>();
            weekInfo.put("week", String.valueOf(current.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
            weekInfo.put("start", startOfWeek.toString());
            weekInfo.put("end", endOfWeek.toString());
            weeks.add(weekInfo);

            // 다음 주로 이동
            current = current.plusWeeks(1);
        }

        return weeks;
    }

    /////// 주차별 지출
    public expenseWeekDTO showWeekExpense(Long memberId, YearMonth month) {
        List<Map<String, String>> weeks = calculateWeeksInMonth(month);

        QAccountHistory accountHistory = QAccountHistory.accountHistory;
        Long accountId = getAccountIdByMemberId(memberId);

        List<BigDecimal> weekExpenses = new ArrayList<>();

        // 주차별 지출 합계 계산
        for (Map<String, String> week : weeks) {
            LocalDate startDate = LocalDate.parse(week.get("start"));
            LocalDate endDate = LocalDate.parse(week.get("end"));

            BigDecimal totalExpense = queryFactory
                    .select(accountHistory.amount.sum())
                    .from(accountHistory)
                    .where(accountHistory.accountId.accountId.eq(accountId)
                            .and(accountHistory.transactionType.eq(TransactionTypeEnum.EXPENSE))
                            .and(accountHistory.transactionDate.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59))))
                    .fetchOne();


            if (totalExpense == null) totalExpense = BigDecimal.ZERO;
            weekExpenses.add(totalExpense);
        }

        // 주차별 지출 정보를 DTO에 담아 반환
        return new expenseWeekDTO(
                weekExpenses.size() > 0 ? weekExpenses.get(0) : BigDecimal.ZERO,
                weekExpenses.size() > 1 ? weekExpenses.get(1) : BigDecimal.ZERO,
                weekExpenses.size() > 2 ? weekExpenses.get(2) : BigDecimal.ZERO,
                weekExpenses.size() > 3 ? weekExpenses.get(3) : BigDecimal.ZERO,
                weekExpenses.size() > 4 ? weekExpenses.get(4) : BigDecimal.ZERO
        );
    }


}
