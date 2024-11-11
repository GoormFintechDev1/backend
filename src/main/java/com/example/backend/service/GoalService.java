package com.example.backend.service;

import com.example.backend.dto.goals.GoalRequestDTO;
import com.example.backend.dto.goals.GoalResponseDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.BusinessRegistration;
import com.example.backend.model.Goals;
import com.example.backend.model.QGoals;
import com.example.backend.repository.GoalsRepository;
import com.example.backend.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j

public class GoalService {

    private final JPAQueryFactory queryFactory;
    private final AccountService accountService;
    private final PosService posService;
    private final MemberRepository memberRepository;
    private final BusinessService businessService;
    private final GoalsRepository goalsRepository;

    // 새로운 목표 설정하기
    public void setGoal(Long memberId, GoalRequestDTO goalRequestDTO) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);

        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(goalRequestDTO.getGoalMonth().withDayOfMonth(1)))
                )
                .fetchOne();

        if (existingGoal != null) {
            throw new BadRequestException("해당 연월에 이미 목표가 설정되어 있습니다.");
        }
        Goals goal = new Goals(
                null,
                business,
                goalRequestDTO.getGoalMonth(),
                goalRequestDTO.getRevenueGoal(),
                goalRequestDTO.getRevenueGoal(),
                false,
                false
        );
        goalsRepository.save(goal);
    }

    // 목표 수정 메서드
    public void updateGoal(Long memberId, GoalRequestDTO goalRequestDTO) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(goalRequestDTO.getGoalMonth().withDayOfMonth(1)))
                )
                .fetchOne();

        if (existingGoal == null) {
            throw new BadRequestException("해당 연월에 설정된 목표가 없습니다.");
        }

        existingGoal.setRevenueGoal(goalRequestDTO.getRevenueGoal());
        existingGoal.setExpenseGoal(goalRequestDTO.getExpenseGoal());
        goalsRepository.save(existingGoal);
    }


    // 목표 달성 여부 체크 및 업데이트
    public GoalResponseDTO goalCheck(Long memberId, LocalDate goalMonth) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);

        QGoals qGoals = QGoals.goals;
        Goals goal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(goalMonth))
                )
                .fetchOne();

        if (goal == null) {
            throw new BadRequestException("설정된 목표가 없습니다.");
        }

        BigDecimal monthlyRevenue = posService.calculateMonthlyRevenue(memberId, YearMonth.from(goalMonth));
        BigDecimal monthlyExpense = accountService.calculateTotalExpenses(YearMonth.from(goalMonth), memberId);

        boolean revenueAchieved = monthlyRevenue.compareTo(goal.getRevenueGoal()) >= 0;
        boolean expenseAchieved = monthlyExpense.compareTo(goal.getExpenseGoal()) <= 0;

//        goalsRepository.save(goal);

        return new GoalResponseDTO(
                goal.getGoalMonth(),
                goal.getRevenueGoal(),
                goal.getExpenseGoal(),
                monthlyRevenue,
                monthlyExpense,
                revenueAchieved,
                expenseAchieved
        );
    }

}
