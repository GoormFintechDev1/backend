package com.example.backend.service;

import com.example.backend.dto.goals.*;
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
import java.time.Year;
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

    // 목표 설정하기

    public void setGoal(Long memberId, GoalRequestDTO requestDTO) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);

        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth()))
                )
                .fetchOne();

        if (existingGoal != null) {
            // 매출 목표가 설정되지 않은 경우 업데이트
            if (requestDTO.getRevenueGoal() != null && existingGoal.getRevenueGoal().compareTo(BigDecimal.ZERO) == 0) {
                existingGoal.setRevenueGoal(requestDTO.getRevenueGoal());
            }
            // 지출 목표가 설정되지 않은 경우 업데이트
            if (requestDTO.getExpenseGoal() != null && existingGoal.getExpenseGoal().compareTo(BigDecimal.ZERO) == 0) {
                existingGoal.setExpenseGoal(requestDTO.getExpenseGoal());
            }

            // 두 목표 모두 이미 설정된 경우 예외 처리
            if (existingGoal.getRevenueGoal().compareTo(BigDecimal.ZERO) > 0 && existingGoal.getExpenseGoal().compareTo(BigDecimal.ZERO) > 0) {
                throw new BadRequestException("해당 연월에 목표가 이미 설정되어 있습니다.");
            }

            goalsRepository.save(existingGoal);
        } else {
            // 새로운 목표 생성, 매출 또는 지출이 null인 경우 기본값 0 설정
            Goals goal = new Goals(
                    null,
                    business,
                    requestDTO.getGoalMonth(),
                    requestDTO.getRevenueGoal() != null ? requestDTO.getRevenueGoal() : BigDecimal.ZERO,
                    requestDTO.getExpenseGoal() != null ? requestDTO.getExpenseGoal() : BigDecimal.ZERO,
                    false,
                    false
            );
            goalsRepository.save(goal);
        }
    }


    // 목표 수정 메서드

    public GoalResponseDTO updateGoal(Long memberId, GoalRequestDTO requestDTO) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth()))
                )
                .fetchOne();

        if (existingGoal == null) {
            throw new BadRequestException("해당 연월에 설정된 목표가 없습니다.");
        }

        // 매출 목표 업데이트 (null이 아닌 경우)
        if (requestDTO.getRevenueGoal() != null) {
            existingGoal.setRevenueGoal(requestDTO.getRevenueGoal());
        }

        // 지출 목표 업데이트 (null이 아닌 경우)
        if (requestDTO.getExpenseGoal() != null) {
            existingGoal.setExpenseGoal(requestDTO.getExpenseGoal());
        }

        goalsRepository.save(existingGoal);

        // 현재 달의 실제 매출 및 지출 데이터 가져오기
        BigDecimal monthlyRevenue = posService.calculateMonthlyRevenue(memberId, requestDTO.getGoalMonth());
        BigDecimal monthlyExpense = accountService.calculateTotalExpenses(requestDTO.getGoalMonth(), memberId);

        // 현재 달의 목표 및 실제 데이터 반환
        return new GoalResponseDTO(
                existingGoal.getGoalMonth(),
                existingGoal.getRevenueGoal(),
                monthlyRevenue,
                existingGoal.getExpenseGoal(),
                monthlyExpense
        );
    }

//
//    // 매출 목표 수정 메서드
//    public void updateRevenueGoal(Long memberId, RevenueGoalRequestDTO requestDTO) {
//        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
//        QGoals qGoals = QGoals.goals;
//        Goals existingGoal = queryFactory
//                .selectFrom(qGoals)
//                .where(
//                        qGoals.businessId.id.eq(business.getId())
//                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth()))
//                )
//                .fetchOne();
//
//        if (existingGoal == null) {
//            throw new BadRequestException("해당 연월에 설정된 목표가 없습니다.");
//        }
//
//        existingGoal.setRevenueGoal(requestDTO.getRevenueGoal());
//        goalsRepository.save(existingGoal);
//    }
//
//    // 지출 목표 수정 메서드
//    public void updateExpenseGoal(Long memberId, ExpenseGoalRequestDTO requestDTO) {
//        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
//        QGoals qGoals = QGoals.goals;
//        Goals existingGoal = queryFactory
//                .selectFrom(qGoals)
//                .where(
//                        qGoals.businessId.id.eq(business.getId())
//                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth()))
//                )
//                .fetchOne();
//
//        if (existingGoal == null) {
//            throw new BadRequestException("해당 연월에 설정된 목표가 없습니다.");
//        }
//
//        existingGoal.setExpenseGoal(requestDTO.getExpenseGoal());
//        goalsRepository.save(existingGoal);
//    }
//

    // 매출 목표 달성 여부 체크 및 업데이트 (3개월)
    public RevenueGoalResponseDTO checkRevenueGoal(Long memberId, YearMonth goalMonth) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        QGoals qGoals = QGoals.goals;

        // 이번 달 목표 및 실제 매출
        Goals currentGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(goalMonth))
                )
                .fetchOne();

        BigDecimal currentMonthRevenue = posService.calculateMonthlyRevenue(memberId, YearMonth.from(goalMonth));

        // 1개월 전 목표 및 실제 매출
        YearMonth oneMonthAgo = goalMonth.minusMonths(1);
        Goals oneMonthGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(oneMonthAgo))
                )
                .fetchOne();
        BigDecimal oneMonthRevenue = posService.calculateMonthlyRevenue(memberId, YearMonth.from(oneMonthAgo));


        // 2개월 전 목표 및 실제 매출
        YearMonth twoMonthsAgo = goalMonth.minusMonths(2);
        Goals twoMonthGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(twoMonthsAgo))
                )
                .fetchOne();
        BigDecimal twoMonthRevenue = posService.calculateMonthlyRevenue(memberId, YearMonth.from(twoMonthsAgo));


        return new RevenueGoalResponseDTO(
                currentGoal != null ? currentGoal.getGoalMonth() : YearMonth.now(),
                twoMonthGoal != null ? twoMonthGoal.getRevenueGoal() : BigDecimal.ZERO,
                twoMonthRevenue != null ? twoMonthRevenue : BigDecimal.ZERO,
                oneMonthGoal != null ? oneMonthGoal.getRevenueGoal() : BigDecimal.ZERO,
                oneMonthRevenue != null ? oneMonthRevenue : BigDecimal.ZERO,
                currentGoal != null ? currentGoal.getRevenueGoal() : BigDecimal.ZERO,
                currentMonthRevenue != null ? currentMonthRevenue : BigDecimal.ZERO
        );
    }


    // 지출 목표 달성 여부 체크 및 업데이트 (3개월)
    public ExpenseGoalResponseDTO checkExpenseGoal(Long memberId, YearMonth goalMonth) {
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        QGoals qGoals = QGoals.goals;

        // 이번 달 목표 및 실제 지출
        Goals currentGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(goalMonth))
                )
                .fetchOne();

        BigDecimal currentMonthExpense = accountService.calculateTotalExpenses(YearMonth.from(goalMonth), memberId);


        // 1개월 전 목표 및 실제 지출
        YearMonth oneMonthAgo = goalMonth.minusMonths(1);
        Goals oneMonthGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(oneMonthAgo))
                )
                .fetchOne();
        BigDecimal oneMonthExpense = accountService.calculateTotalExpenses(YearMonth.from(oneMonthAgo), memberId);

        // 2개월 전 목표 및 실제 지출
        YearMonth twoMonthsAgo = goalMonth.minusMonths(2);
        Goals twoMonthGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessId.id.eq(business.getId())
                                .and(qGoals.goalMonth.eq(twoMonthsAgo))
                )
                .fetchOne();
        BigDecimal twoMonthExpense = accountService.calculateTotalExpenses(YearMonth.from(twoMonthsAgo), memberId);

        return new ExpenseGoalResponseDTO(
                currentGoal != null ? currentGoal.getGoalMonth() : YearMonth.now(),
                twoMonthGoal != null ? twoMonthGoal.getExpenseGoal() : BigDecimal.ZERO,
                twoMonthExpense != null ? twoMonthExpense : BigDecimal.ZERO,
                oneMonthGoal != null ? oneMonthGoal.getExpenseGoal() : BigDecimal.ZERO,
                oneMonthExpense != null ? oneMonthExpense : BigDecimal.ZERO,
                currentGoal != null ? currentGoal.getExpenseGoal() : BigDecimal.ZERO,
                currentMonthExpense != null ? currentMonthExpense : BigDecimal.ZERO
        );
    }

}
