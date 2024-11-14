package com.example.backend.service;

import com.example.backend.dto.pos.DailyIncomeDTO;
import com.example.backend.dto.pos.IncomeHistoryDTO;
import com.example.backend.dto.pos.MonthlyIncomeDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.*;
import com.example.backend.model.enumSet.PaymentTypeEnum;
import com.example.backend.model.enumSet.TransactionMeansEnum;
import com.example.backend.model.enumSet.TransactionTypeEnum;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosService {

    private final JPAQueryFactory queryFactory;

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

    // 월말 정산하여 Account에 입금하는 함수
    public void depositMonthlyIncomeToAccount(Long memberId, YearMonth month) {
        Long posId = getPosIdByMemberId(memberId);

        QPosSales qPosSales = QPosSales.posSales;
        QAccount qAccount = QAccount.account;
        QBusinessRegistration qBusiness = QBusinessRegistration.businessRegistration;
        QAccountHistory qAccountHistory = QAccountHistory.accountHistory;

        // 1. 월 매출 합계 조회
        BigDecimal monthlyTotalIncome = queryFactory
                .select(qPosSales.totalAmount.sum())
                .from(qPosSales)
                .where(qPosSales.pos.posId.eq(posId)
                        .and(qPosSales.saleDate.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        if (monthlyTotalIncome == null || monthlyTotalIncome.compareTo(BigDecimal.ZERO) == 0) {
            log.info("해당 월에 매출이 없습니다.");
            return;
        }

        // 2. Account 조회
        Account account = queryFactory
                .selectFrom(qAccount)
                .join(qAccount.business, qBusiness)
                .where(qBusiness.member.id.eq(memberId))
                .fetchOne();

        if (account == null) {
            throw new BadRequestException("해당 사업자의 계좌가 없습니다.");
        }

        // 3. Account의 잔고 업데이트
        BigDecimal updatedBalance = account.getBalance().add(monthlyTotalIncome);
        account.setBalance(updatedBalance);

        // 4. AccountHistory 기록 추가
        saveRevenueHistory(account, monthlyTotalIncome, updatedBalance);
        log.info("매출 정산 완료: {}원 입금됨 (Account ID: {}), 현재 잔액: {}원", monthlyTotalIncome, account.getAccountId(), updatedBalance);
    }

    private void saveRevenueHistory(Account account, BigDecimal amount, BigDecimal balanceAfter) {
        QAccountHistory qAccountHistory = QAccountHistory.accountHistory;

        long insertedCount = queryFactory
                .insert(qAccountHistory)
                .set(qAccountHistory.accountId, account)
                .set(qAccountHistory.transactionType, TransactionTypeEnum.REVENUE)
                .set(qAccountHistory.transactionMeans, TransactionMeansEnum.CARD)
                .set(qAccountHistory.transactionDate, LocalDateTime.now())
                .set(qAccountHistory.amount, amount)
                .set(qAccountHistory.balanceAfter, balanceAfter)
                .set(qAccountHistory.category, "매출 정산")
                .set(qAccountHistory.note, "월말 매출 정산 기록")
                .set(qAccountHistory.fixedExpenses, false)
                .set(qAccountHistory.storeName, "정산 시스템")
                .execute();

        if (insertedCount > 0) {
            log.info("AccountHistory에 매출 내역 기록 완료 (Account ID: {})", account.getAccountId());
        } else {
            log.error("AccountHistory 기록 실패");
        }
    }


    // 월 매출 요약 및 일별 매출 리스트 반환
    public MonthlyIncomeDTO getMonthlyIncomeSummary(Long memberId, YearMonth month) {
        Long posId = getPosIdByMemberId(memberId);

        QPos qPos = QPos.pos;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;
        QPosSales qposSales = QPosSales.posSales;

        Boolean isAuthorized = queryFactory
                .selectOne()
                .from(qPos)
                .join(qPos.businessRegistration, qBusinessRegistration)
                .where(
                        qBusinessRegistration.member.id.eq(memberId)
                                .and(qPos.posId.eq(posId))
                )
                .fetchFirst() != null;

        if (!isAuthorized) {
            throw new BadRequestException("포스 접근 권한이 없음.");
        }

        BigDecimal monthlyTotalIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal monthlyCardIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CARD))
                        .and(qposSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal monthlyCashIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CASH))
                        .and(qposSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        List<DailyIncomeDTO> dailyIncomeList = queryFactory
                .select(
                        qposSales.saleDate,
                        qposSales.totalAmount.sum(),
                        new CaseBuilder()
                                .when(qposSales.paymentType.eq(PaymentTypeEnum.CARD))
                                .then(qposSales.totalAmount.sum())
                                .otherwise(BigDecimal.ZERO),
                        new CaseBuilder()
                                .when(qposSales.paymentType.eq(PaymentTypeEnum.CASH))
                                .then(qposSales.totalAmount.sum())
                                .otherwise(BigDecimal.ZERO)
                )
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .groupBy(qposSales.saleDate,
                        qposSales.saleDate.year(),
                        qposSales.saleDate.month(),
                        qposSales.saleDate.dayOfMonth(),
                        qposSales.paymentType
                )
                .fetch()
                .stream()
                .map(tuple -> new DailyIncomeDTO(
                        tuple.get(qposSales.saleDate).toLocalDate(),
                        tuple.get(qposSales.totalAmount.sum()),
                        tuple.get(new CaseBuilder()
                                .when(qposSales.paymentType.eq(PaymentTypeEnum.CARD))
                                .then(qposSales.totalAmount.sum())
                                .otherwise(BigDecimal.ZERO)),
                        tuple.get(new CaseBuilder()
                                .when(qposSales.paymentType.eq(PaymentTypeEnum.CASH))
                                .then(qposSales.totalAmount.sum())
                                .otherwise(BigDecimal.ZERO))
                ))
                .collect(Collectors.toList());

        return new MonthlyIncomeDTO(
                monthlyTotalIncome != null ? monthlyTotalIncome : BigDecimal.ZERO,
                monthlyCardIncome != null ? monthlyCardIncome : BigDecimal.ZERO,
                monthlyCashIncome != null ? monthlyCashIncome : BigDecimal.ZERO,
                dailyIncomeList
        );
    }

    // 특정 일 매출 세부 정보 반환
    public DailyIncomeDTO getDailyIncomeDetail(Long memberId, LocalDate date) {
        Long posId = getPosIdByMemberId(memberId);

        QPos qPos = QPos.pos;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;
        QPosSales qposSales = QPosSales.posSales;

        Boolean isAuthorized = queryFactory
                .selectOne()
                .from(qPos)
                .join(qPos.businessRegistration, qBusinessRegistration)
                .where(
                        qBusinessRegistration.member.id.eq(memberId)
                                .and(qPos.posId.eq(posId))
                )
                .fetchFirst() != null;

        if (!isAuthorized) {
            throw new BadRequestException("포스 접근 권한이 없음.");
        }

        BigDecimal totalIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.year().eq(date.getYear()))
                        .and(qposSales.saleDate.month().eq(date.getMonthValue()))
                        .and(qposSales.saleDate.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();

        BigDecimal cardIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CARD))
                        .and(qposSales.saleDate.year().eq(date.getYear()))
                        .and(qposSales.saleDate.month().eq(date.getMonthValue()))
                        .and(qposSales.saleDate.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();

        BigDecimal cashIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CASH))
                        .and(qposSales.saleDate.year().eq(date.getYear()))
                        .and(qposSales.saleDate.month().eq(date.getMonthValue()))
                        .and(qposSales.saleDate.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();

        return new DailyIncomeDTO(
                date,
                totalIncome != null ? totalIncome : BigDecimal.ZERO,
                cardIncome != null ? cardIncome : BigDecimal.ZERO,
                cashIncome != null ? cashIncome : BigDecimal.ZERO
        );
    }

    // 이번 달 매출 및 지난 2개월의 월 매출 정보
    public IncomeHistoryDTO getIncomeHistory(Long memberId, YearMonth month) {
        Long posId = getPosIdByMemberId(memberId);

        QPos qPos = QPos.pos;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;
        QPosSales qposSales = QPosSales.posSales;

        Boolean isAuthorized = queryFactory
                .selectOne()
                .from(qPos)
                .join(qPos.businessRegistration, qBusinessRegistration)
                .where(
                        qBusinessRegistration.member.id.eq(memberId)
                                .and(qPos.posId.eq(posId))
                )
                .fetchFirst() != null;

        if (!isAuthorized) {
            throw new BadRequestException("포스 접근 권한이 없음.");
        }

        // 이번 달 매출
        BigDecimal totalIncome0Ago = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        // 1개월 전 매출
        YearMonth oneMonthAgo = month.minusMonths(1);
        BigDecimal totalIncome1Ago = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.between(
                                oneMonthAgo.atDay(1).atStartOfDay(),
                                oneMonthAgo.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        // 2개월 전 매출
        YearMonth twoMonthsAgo = month.minusMonths(2);
        BigDecimal totalIncome2Ago = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.between(
                                twoMonthsAgo.atDay(1).atStartOfDay(),
                                twoMonthsAgo.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        return new IncomeHistoryDTO(
                totalIncome2Ago != null ? totalIncome2Ago : BigDecimal.ZERO,
                totalIncome1Ago != null ? totalIncome1Ago : BigDecimal.ZERO,
                totalIncome0Ago != null ? totalIncome0Ago : BigDecimal.ZERO

        );
    }

    public BigDecimal calculateMonthlyRevenue(Long memberId, YearMonth month) {
        Long posId = getPosIdByMemberId(memberId);
        QPosSales qposSales = QPosSales.posSales;

        return queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

    }

    public Map<String, BigDecimal> calculateAverageMonthlyMetrics(YearMonth month) {
        QPosSales qPosSales = QPosSales.posSales;

        // 1. 전체 사업자의 월 매출 평균
        BigDecimal totalMonthlyIncome = queryFactory
                .select(qPosSales.totalAmount.sum())
                .from(qPosSales)
                .where(qPosSales.saleDate.between(
                        month.atDay(1).atStartOfDay(),
                        month.atEndOfMonth().atTime(23, 59, 59)))
                .fetchOne();

        BigDecimal averageMonthlyIncome = totalMonthlyIncome != null
                ? totalMonthlyIncome.divide(BigDecimal.valueOf(5), RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 2. 전체 사업자의 월 카드 매출 평균
        BigDecimal totalCardIncome = queryFactory
                .select(qPosSales.totalAmount.sum())
                .from(qPosSales)
                .where(qPosSales.paymentType.eq(PaymentTypeEnum.CARD)
                        .and(qPosSales.saleDate.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal averageMonthlyCardIncome = totalCardIncome != null
                ? totalCardIncome.divide(BigDecimal.valueOf(5), RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 3. 전체 사업자의 월 현금 매출 평균
        BigDecimal totalCashIncome = queryFactory
                .select(qPosSales.totalAmount.sum())
                .from(qPosSales)
                .where(qPosSales.paymentType.eq(PaymentTypeEnum.CASH)
                        .and(qPosSales.saleDate.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal averageMonthlyCashIncome = totalCashIncome != null
                ? totalCashIncome.divide(BigDecimal.valueOf(5), RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 4. 전체 사업자의 평균 결제 시간 (초 단위로 계산 후 분 단위로 변환)
        Double avgHourInSeconds = queryFactory.select(qPosSales.saleTime.hour().avg().multiply(3600).doubleValue()).from(qPosSales)
                .where(qPosSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59)))
                .fetchOne();

        Double avgMinuteInSeconds = queryFactory.select(qPosSales.saleTime.minute().avg().multiply(60).doubleValue()).from(qPosSales)
                .where(qPosSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59)))
                .fetchOne();

        Double avgSecond = queryFactory.select(qPosSales.saleTime.second().avg().doubleValue()).from(qPosSales)
                .where(qPosSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59)))
                .fetchOne();

        // 결제 시간을 분 단위로 변환
        BigDecimal averagePaymentTime = BigDecimal.valueOf((avgHourInSeconds != null ? avgHourInSeconds : 0) +
                        (avgMinuteInSeconds != null ? avgMinuteInSeconds : 0) +
                        (avgSecond != null ? avgSecond : 0))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        // 결과를 Map에 담아 반환

        // 결과를 Map에 담아 반환
        Map<String, BigDecimal> averageMetrics = new HashMap<>();
        averageMetrics.put("averageMonthlyIncome", averageMonthlyIncome);
        averageMetrics.put("averageMonthlyCardIncome", averageMonthlyCardIncome);
        averageMetrics.put("averageMonthlyCashIncome", averageMonthlyCashIncome);
        averageMetrics.put("averagePaymentTimeMinutes", averagePaymentTime);

        return averageMetrics;
    }

}