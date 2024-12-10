package com.example.backend.service.POS;

import com.example.backend.dto.pos.DailyIncomeDTO;
import com.example.backend.dto.pos.IncomeHistoryDTO;
import com.example.backend.dto.pos.MonthlyIncomeDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.BUSINESS.QBusinessRegistration;
import com.example.backend.model.POS.QPos;
import com.example.backend.model.POS.QPosSales;
import com.example.backend.model.QMember;
import com.example.backend.model.enumSet.PaymentTypeEnum;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
        QMember qMember = QMember.member;

        Long posId = queryFactory
                .select(qPos.posId)
                .from(qMember)
                .join(qMember.businessRegistration, qBusinessRegistration)
                .join(qBusinessRegistration.pos, qPos)
                .where(qMember.memberId.eq(memberId))
                .fetchOne();

        if (posId == null) {
            throw new BadRequestException("해당 사용자는 포스가 없습니다.");
        }
        return posId;
    }


    // 월 매출 요약 및 일별 매출 리스트 반환
    public MonthlyIncomeDTO getMonthlyIncomeSummary(Long memberId, YearMonth month) {
        Long posId = getPosIdByMemberId(memberId);
        QPosSales qposSales = QPosSales.posSales;

        BigDecimal monthlyTotalIncome = queryFactory
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.orderTime.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal monthlyCardIncome = queryFactory
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CARD))
                        .and(qposSales.orderTime.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal monthlyCashIncome = queryFactory
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CASH))
                        .and(qposSales.orderTime.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        List<DailyIncomeDTO> dailyIncomeList = queryFactory
                .select(
                        qposSales.orderTime,
                        qposSales.totalPrice.sum(),
                        new CaseBuilder()
                                .when(qposSales.paymentType.eq(PaymentTypeEnum.CARD))
                                .then(qposSales.totalPrice.sum())
                                .otherwise(BigDecimal.ZERO),
                        new CaseBuilder()
                                .when(qposSales.paymentType.eq(PaymentTypeEnum.CASH))
                                .then(qposSales.totalPrice.sum())
                                .otherwise(BigDecimal.ZERO)
                )
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.orderTime.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .groupBy(qposSales.orderTime,
                        qposSales.orderTime.year(),
                        qposSales.orderTime.month(),
                        qposSales.orderTime.dayOfMonth(),
                        qposSales.paymentType
                )
                .fetch()
                .stream()
                .map(tuple -> new DailyIncomeDTO(
                        tuple.get(qposSales.orderTime).toLocalDate(),
                        tuple.get(qposSales.totalPrice.sum()),
                        tuple.get(new CaseBuilder()
                                .when(qposSales.paymentType.eq(PaymentTypeEnum.CARD))
                                .then(qposSales.totalPrice.sum())
                                .otherwise(BigDecimal.ZERO)),
                        tuple.get(new CaseBuilder()
                                .when(qposSales.paymentType.eq(PaymentTypeEnum.CASH))
                                .then(qposSales.totalPrice.sum())
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
        QPosSales qposSales = QPosSales.posSales;


        BigDecimal totalIncome = queryFactory
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.orderTime.year().eq(date.getYear()))
                        .and(qposSales.orderTime.month().eq(date.getMonthValue()))
                        .and(qposSales.orderTime.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();

        BigDecimal cardIncome = queryFactory
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CARD))
                        .and(qposSales.orderTime.year().eq(date.getYear()))
                        .and(qposSales.orderTime.month().eq(date.getMonthValue()))
                        .and(qposSales.orderTime.dayOfMonth().eq(date.getDayOfMonth())))
                .fetchOne();

        BigDecimal cashIncome = queryFactory
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.paymentType.eq(PaymentTypeEnum.CASH))
                        .and(qposSales.orderTime.year().eq(date.getYear()))
                        .and(qposSales.orderTime.month().eq(date.getMonthValue()))
                        .and(qposSales.orderTime.dayOfMonth().eq(date.getDayOfMonth())))
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
        QPosSales qposSales = QPosSales.posSales;


        // 이번 달 매출
        BigDecimal totalIncome0Ago = queryFactory
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.orderTime.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        // 1개월 전 매출
        YearMonth oneMonthAgo = month.minusMonths(1);
        BigDecimal totalIncome1Ago = queryFactory
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.orderTime.between(
                                oneMonthAgo.atDay(1).atStartOfDay(),
                                oneMonthAgo.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        // 2개월 전 매출
        YearMonth twoMonthsAgo = month.minusMonths(2);
        BigDecimal totalIncome2Ago = queryFactory
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.orderTime.between(
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
                .select(qposSales.totalPrice.sum())
                .from(qposSales)
                .where(qposSales.posId.posId.eq(posId)
                        .and(qposSales.orderTime.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

    }

    public Map<String, Object> calculateAverageMonthlyMetrics(YearMonth month) {
        QPosSales qPosSales = QPosSales.posSales;

        // 1. 전체 사업자의 월 매출 평균
        BigDecimal totalMonthlyIncome = queryFactory
                .select(qPosSales.totalPrice.sum())
                .from(qPosSales)
                .where(qPosSales.orderTime.between(
                        month.atDay(1).atStartOfDay(),
                        month.atEndOfMonth().atTime(23, 59, 59)))
                .fetchOne();

        BigDecimal averageMonthlyIncome = totalMonthlyIncome != null
                ? totalMonthlyIncome.divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 2. 전체 사업자의 월 카드 매출 평균
        BigDecimal totalCardIncome = queryFactory
                .select(qPosSales.totalPrice.sum())
                .from(qPosSales)
                .where(qPosSales.paymentType.eq(PaymentTypeEnum.CARD)
                        .and(qPosSales.orderTime.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal averageMonthlyCardIncome = totalCardIncome != null
                ? totalCardIncome.divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 3. 전체 사업자의 월 현금 매출 평균
        BigDecimal totalCashIncome = queryFactory
                .select(qPosSales.totalPrice.sum())
                .from(qPosSales)
                .where(
                        qPosSales.paymentType.eq(PaymentTypeEnum.CASH)
                        .and(qPosSales.orderTime.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal averageMonthlyCashIncome = totalCashIncome != null
                ? totalCashIncome.divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 4. 아침, 점심, 저녁 시간대별 매출 합계 계산
        BigDecimal morningSales = queryFactory
                .select(qPosSales.totalPrice.sum())
                .from(qPosSales)
                .where(qPosSales.orderTime.hour().between(6, 11)
                        .and(qPosSales.orderTime.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal afternoonSales = queryFactory
                .select(qPosSales.totalPrice.sum())
                .from(qPosSales)
                .where(qPosSales.orderTime.hour().between(12, 17)
                        .and(qPosSales.orderTime.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        BigDecimal eveningSales = queryFactory
                .select(qPosSales.totalPrice.sum())
                .from(qPosSales)
                .where(qPosSales.orderTime.hour().between(18, 23)
                        .and(qPosSales.orderTime.between(
                                month.atDay(1).atStartOfDay(),
                                month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();

        // Null 처리
        morningSales = morningSales != null ? morningSales : BigDecimal.ZERO;
        afternoonSales = afternoonSales != null ? afternoonSales : BigDecimal.ZERO;
        eveningSales = eveningSales != null ? eveningSales : BigDecimal.ZERO;

        // 5. 매출이 가장 높은 시간대 판별
        String peakSalesPeriod;
        if (morningSales.compareTo(afternoonSales) > 0 && morningSales.compareTo(eveningSales) > 0) {
            peakSalesPeriod = "Morning (06:00 - 11:59)";
        } else if (afternoonSales.compareTo(morningSales) > 0 && afternoonSales.compareTo(eveningSales) > 0) {
            peakSalesPeriod = "Afternoon (12:00 - 17:59)";
        } else if (eveningSales.compareTo(morningSales) > 0 && eveningSales.compareTo(afternoonSales) > 0) {
            peakSalesPeriod = "Evening (18:00 - 23:59)";
        } else {
            peakSalesPeriod = "No peak time";
        }

        // 6. 결과를 Map에 담아 반환
        Map<String, Object> averageMetrics = new HashMap<>();
        averageMetrics.put("averageMonthlyIncome", averageMonthlyIncome);
        averageMetrics.put("averageMonthlyCardIncome", averageMonthlyCardIncome);
        averageMetrics.put("averageMonthlyCashIncome", averageMonthlyCashIncome);
        averageMetrics.put("morningSales", morningSales);
        averageMetrics.put("afternoonSales", afternoonSales);
        averageMetrics.put("eveningSales", eveningSales);

        // 추가적으로 최고 매출 시간대 정보도 Map에 담음
        averageMetrics.put("peakSalesPeriod", peakSalesPeriod); // peakSalesPeriod를 문자열로 저장

        return averageMetrics;
    }

}
