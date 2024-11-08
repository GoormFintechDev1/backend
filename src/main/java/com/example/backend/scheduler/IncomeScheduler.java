package com.example.backend.scheduler;

import com.example.backend.model.QMember;
import com.example.backend.service.PosService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.YearMonth;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncomeScheduler {
    private final JPAQueryFactory queryFactory;
    private final PosService posService;

    @Scheduled(cron = "0 0 0 L * ?")  // 매월 마지막 날 자정
    public void monthlyIncomeDeposit() {
        YearMonth currentMonth = YearMonth.now();
        List<Long> memberIds = getAllMemberIds();

        // 모든 회원의 매출 정산하기
        for (Long memberId : memberIds) {
            try {
                posService.depositMonthlyIncomeToAccount(memberId, currentMonth);
                log.info("회원 ID {}의 매출 정산 완료", memberId);
            } catch (Exception e) {
                log.error("회원 ID {}의 정산 중 오류 발생: {}", memberId, e.getMessage());
            }
        }
    }
    private List<Long> getAllMemberIds() {
        return queryFactory
                .select(QMember.member.id)
                .from(QMember.member)
                .fetch();
    }
}
