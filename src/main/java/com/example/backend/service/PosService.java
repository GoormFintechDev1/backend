package com.example.backend.service;

import com.example.backend.dto.pos.DailyIncomeDTO;
import com.example.backend.dto.pos.MonthlyIncomeDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.*;
import com.example.backend.model.enumSet.PaymentTypeEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosService {

    private final JPAQueryFactory queryFactory;

    // 월 매출 요약
    public MonthlyIncomeDTO getMonthlyIncomeSummary(Long memberId, Long posId, YearMonth month) {

        QPos qPos = QPos.pos;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;
        QPosSales qposSales = QPosSales.posSales;

        // QueryDSL을 사용하여 memberId, posId, 그리고 businessId가 연결된 관계인지 확인
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


        // 월 매출 총합 계산
        BigDecimal monthlyTotalIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        // 월 카드 매출 총합 계산
        BigDecimal monthlyCardIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CARD))
                        .and(qposSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        // 월 현금 매출 총합 계산
        BigDecimal monthlyCashIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CASH))
                        .and(qposSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();





        return new MonthlyIncomeDTO(
                monthlyTotalIncome != null ? monthlyTotalIncome : BigDecimal.ZERO,
                monthlyCardIncome != null ? monthlyCardIncome : BigDecimal.ZERO,
                monthlyCashIncome != null ? monthlyCashIncome : BigDecimal.ZERO
        );
    }


    // 특정 일 매출 세부 정보
    public DailyIncomeDTO getDailyIncomeDetail(Long memberId, Long posId, LocalDate date) {
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

}
