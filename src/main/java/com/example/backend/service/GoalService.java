package com.example.backend.service;

import com.example.backend.dto.goals.ExpenseGoalRequestDTO;
import com.example.backend.dto.goals.ExpenseGoalResponseDTO;
import com.example.backend.dto.goals.RevenueGoalResponseDTO;
import com.example.backend.dto.goals.RevenueGoalRequestDTO;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.BusinessRegistration;
import com.example.backend.model.Goals;
import com.example.backend.model.QGoals;
import com.example.backend.repository.GoalsRepository;
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
    private final BusinessService businessService;
    private final GoalsRepository goalsRepository;

    // 새로운 매출 목표 설정하기
    public void setRevenueGoal(Long memberId, RevenueGoalRequestDTO requestDTO) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);

        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth().withDayOfMonth(1)))
                )
                .fetchOne();

        if (existingGoal != null) {
            // 목표가 이미 존재하지만 매출 목표가 설정되지 않은 경우 업데이트
            if (existingGoal.getRevenueGoal().compareTo(BigDecimal.ZERO) == 0) {
                existingGoal.setRevenueGoal(requestDTO.getRevenueGoal());
                goalsRepository.save(existingGoal);
            } else {
                throw new BadRequestException("해당 연월에 매출 목표가 이미 설정되어 있습니다.");
            }
        } else {
            Goals goal = new Goals(
                    null,
                    business,
                    requestDTO.getGoalMonth(),
                    requestDTO.getRevenueGoal(),
                    BigDecimal.ZERO,
                    false,
                    false
            );
            goalsRepository.save(goal);
        }
    }

    // 새로운 지출 목표 설정하기
    public void setExpenseGoal(Long memberId, ExpenseGoalRequestDTO requestDTO) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);

        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth().withDayOfMonth(1)))
                )
                .fetchOne();

        if (existingGoal != null) {
            // 목표가 이미 존재하지만 지출 목표가 설정되지 않은 경우 업데이트
            if (existingGoal.getExpenseGoal().compareTo(BigDecimal.ZERO) == 0) {
                existingGoal.setExpenseGoal(requestDTO.getExpenseGoal());
                goalsRepository.save(existingGoal);
            } else {
                throw new BadRequestException("해당 연월에 지출 목표가 이미 설정되어 있습니다.");
            }
        } else {
            // 새로운 목표 생성
            Goals goal = new Goals(
                    null,
                    business,
                    requestDTO.getGoalMonth(),
                    BigDecimal.ZERO,
                    requestDTO.getExpenseGoal(),
                    false,
                    false
            );
            goalsRepository.save(goal);
        }
    }

    // 매출 목표 수정 메서드
    public void updateRevenueGoal(Long memberId, RevenueGoalRequestDTO requestDTO) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth().withDayOfMonth(1)))
                )
                .fetchOne();

        if (existingGoal == null) {
            throw new BadRequestException("해당 연월에 설정된 목표가 없습니다.");
        }

        existingGoal.setRevenueGoal(requestDTO.getRevenueGoal());
        goalsRepository.save(existingGoal);
    }

    // 지출 목표 수정 메서드
    public void updateExpenseGoal(Long memberId, ExpenseGoalRequestDTO requestDTO) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth().withDayOfMonth(1)))
                )
                .fetchOne();

        if (existingGoal == null) {
            throw new BadRequestException("해당 연월에 설정된 목표가 없습니다.");
        }

        existingGoal.setExpenseGoal(requestDTO.getExpenseGoal());
        goalsRepository.save(existingGoal);
    }

    // 매출 목표 달성 여부 체크 및 업데이트
    public RevenueGoalResponseDTO checkRevenueGoal(Long memberId, LocalDate goalMonth) {
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
        boolean revenueAchieved = monthlyRevenue.compareTo(goal.getRevenueGoal()) >= 0;

        return new RevenueGoalResponseDTO(
                goal.getGoalMonth(),
                goal.getRevenueGoal(),
                monthlyRevenue,
                revenueAchieved
        );
    }

    // 지출 목표 달성 여부 체크 및 업데이트
    public ExpenseGoalResponseDTO checkExpenseGoal(Long memberId, LocalDate goalMonth) {
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

        BigDecimal monthlyExpense = accountService.calculateTotalExpenses(YearMonth.from(goalMonth), memberId);
        boolean expenseAchieved = monthlyExpense.compareTo(goal.getExpenseGoal()) <= 0;

        return new ExpenseGoalResponseDTO(
                goal.getGoalMonth(),
                goal.getExpenseGoal(),
                monthlyExpense,
                expenseAchieved
        );
    }
}
