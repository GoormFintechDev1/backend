package com.example.backend.service;

import com.example.backend.dto.pos.DailyIncomeDTO;
import com.example.backend.dto.pos.MonthlySalesSummaryDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.*;
import com.example.backend.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    // 월별 총 매출 합계 계산 및 Pos income 업데이트
    public MonthlySalesSummaryDTO getMonthlySalesSummary(Long memberId, Long posId, YearMonth month) {

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
