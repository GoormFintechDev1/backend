package com.example.backend.service;

import com.example.backend.dto.pos.DailyIncomeDTO;
import com.example.backend.dto.pos.MonthlySalesSummaryDTO;
import com.example.backend.model.QPosSales;
import com.example.backend.repository.PosRepository;
import com.example.backend.repository.PosSalesRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosService {

    private final JPAQueryFactory queryFactory;
    private final PosRepository posRepository;
    private final PosSalesRepository posSalesRepository;

    // 월별 총 매출 합계 계산 및 Pos income 업데이트
    public MonthlySalesSummaryDTO getMonthlySalesSummary(Long posId, YearMonth month) {

        QPosSales qposSales = QPosSales.posSales;

        // 월 매출 총합 계산
        BigDecimal monthlyIncome = queryFactory
                .select(qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .fetchOne();



        // 일별 매출 리스트 생성
        List<DailyIncomeDTO> dailyIncomeList = queryFactory
                .select(qposSales.saleDate, qposSales.totalAmount.sum())
                .from(qposSales)
                .where(qposSales.pos.posId.eq(posId)
                        .and(qposSales.saleDate.between(month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(23, 59, 59))))
                .groupBy(qposSales.saleDate.year(), qposSales.saleDate.month(), qposSales.saleDate.dayOfMonth()) // 일별 그룹화
                .fetch()
                .stream()
                .map(tuple -> new DailyIncomeDTO(
                        tuple.get(qposSales.saleDate).toLocalDate(),
                        tuple.get(qposSales.totalAmount.sum())
                ))
                .collect(Collectors.toList());

        return new MonthlySalesSummaryDTO(monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO, dailyIncomeList);
    }

}
