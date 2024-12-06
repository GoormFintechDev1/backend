package com.example.backend.service.BUSINESS;

import com.example.backend.dto.goals.*;
import com.example.backend.exception.base_exceptions.BadRequestException;
import com.example.backend.model.BUSINESS.BusinessRegistration;
import com.example.backend.model.BUSINESS.Goals;
import com.example.backend.model.BUSINESS.QGoals;
import com.example.backend.repository.GoalsRepository;
import com.example.backend.service.BANK.AccountService;
import com.example.backend.service.POS.PosService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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
        log.info("Setting goal for memberId: {}, requestDTO: {}", memberId, requestDTO);
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        log.info("Fetched businessRegistration for memberId: {}, businessId: {}", memberId, business.getBusinessRegistrationId());

        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth()))
                )
                .fetchOne();

        if (existingGoal != null) {
            log.info("Existing goal found for businessId: {}, goalMonth: {}", business.getBusinessRegistrationId(), requestDTO.getGoalMonth());
            if (requestDTO.getRevenueGoal() != null && existingGoal.getRevenueGoal().compareTo(BigDecimal.ZERO) == 0) {
                log.info("Updating revenue goal to: {}", requestDTO.getRevenueGoal());
                existingGoal.setRevenueGoal(requestDTO.getRevenueGoal());
            }
            if (requestDTO.getExpenseGoal() != null && existingGoal.getExpenseGoal().compareTo(BigDecimal.ZERO) == 0) {
                log.info("Updating expense goal to: {}", requestDTO.getExpenseGoal());
                existingGoal.setExpenseGoal(requestDTO.getExpenseGoal());
            }
            if (existingGoal.getRevenueGoal().compareTo(BigDecimal.ZERO) > 0 && existingGoal.getExpenseGoal().compareTo(BigDecimal.ZERO) > 0) {
                log.warn("Goal already set for month: {}", requestDTO.getGoalMonth());
                throw new BadRequestException("해당 연월에 목표가 이미 설정되어 있습니다.");
            }

            goalsRepository.save(existingGoal);
            log.info("Updated existing goal: {}", existingGoal);
        } else {
            Goals goal = new Goals(
                    null,
                    business,
                    requestDTO.getGoalMonth(),
                    requestDTO.getRevenueGoal() != null ? requestDTO.getRevenueGoal() : BigDecimal.ZERO,
                    requestDTO.getExpenseGoal() != null ? requestDTO.getExpenseGoal() : BigDecimal.ZERO
            );
            goalsRepository.save(goal);
            log.info("Created new goal: {}", goal);
        }
    }

    // 목표 수정 메서드
    public GoalResponseDTO updateGoal(Long memberId, GoalRequestDTO requestDTO) {
        log.info("Updating goal for memberId: {}, requestDTO: {}", memberId, requestDTO);
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        log.info("Fetched businessRegistration for memberId: {}, businessId: {}", memberId, business.getBusinessRegistrationId());

        QGoals qGoals = QGoals.goals;
        Goals existingGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                .and(qGoals.goalMonth.eq(requestDTO.getGoalMonth()))
                )
                .fetchOne();

        if (existingGoal == null) {
            log.error("No existing goal found for month: {}", requestDTO.getGoalMonth());
            throw new BadRequestException("해당 연월에 설정된 목표가 없습니다.");
        }

        if (requestDTO.getRevenueGoal() != null) {
            log.info("Updating revenue goal to: {}", requestDTO.getRevenueGoal());
            existingGoal.setRevenueGoal(requestDTO.getRevenueGoal());
        }

        if (requestDTO.getExpenseGoal() != null) {
            log.info("Updating expense goal to: {}", requestDTO.getExpenseGoal());
            existingGoal.setExpenseGoal(requestDTO.getExpenseGoal());
        }

        goalsRepository.save(existingGoal);
        log.info("Updated goal: {}", existingGoal);

        BigDecimal monthlyRevenue = posService.calculateMonthlyRevenue(memberId, requestDTO.getGoalMonth());
        log.info("Monthly revenue for memberId: {}, month: {}, revenue: {}", memberId, requestDTO.getGoalMonth(), monthlyRevenue);

        BigDecimal monthlyExpense = accountService.calculateTotalExpenses(requestDTO.getGoalMonth(), memberId);
        log.info("Monthly expense for memberId: {}, month: {}, expense: {}", memberId, requestDTO.getGoalMonth(), monthlyExpense);

        return new GoalResponseDTO(
                existingGoal.getGoalMonth(),
                existingGoal.getRevenueGoal(),
                monthlyRevenue,
                existingGoal.getExpenseGoal(),
                monthlyExpense
        );
    }

    // 매출 목표 달성 여부 체크
    public RevenueGoalResponseDTO checkRevenueGoal(Long memberId, YearMonth goalMonth) {
        log.info("Checking revenue goal for memberId: {}, goalMonth: {}", memberId, goalMonth);
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        log.info("Fetched businessRegistration for memberId: {}, businessId: {}", memberId, business.getBusinessRegistrationId());

        QGoals qGoals = QGoals.goals;

        Goals currentGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                .and(qGoals.goalMonth.eq(goalMonth))
                )
                .fetchOne();

        BigDecimal currentMonthRevenue = posService.calculateMonthlyRevenue(memberId, goalMonth);
        log.info("Current month revenue for memberId: {}, goalMonth: {}, revenue: {}", memberId, goalMonth, currentMonthRevenue);

        YearMonth oneMonthAgo = goalMonth.minusMonths(1);
        Goals oneMonthGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                .and(qGoals.goalMonth.eq(oneMonthAgo))
                )
                .fetchOne();

        YearMonth twoMonthsAgo = goalMonth.minusMonths(2);
        Goals twoMonthGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                .and(qGoals.goalMonth.eq(twoMonthsAgo))
                )
                .fetchOne();

        return new RevenueGoalResponseDTO(
                currentGoal != null ? currentGoal.getGoalMonth() : YearMonth.now(),
                twoMonthGoal != null ? twoMonthGoal.getRevenueGoal() : BigDecimal.ZERO,
                posService.calculateMonthlyRevenue(memberId, twoMonthsAgo),
                oneMonthGoal != null ? oneMonthGoal.getRevenueGoal() : BigDecimal.ZERO,
                posService.calculateMonthlyRevenue(memberId, oneMonthAgo),
                currentGoal != null ? currentGoal.getRevenueGoal() : BigDecimal.ZERO,
                currentMonthRevenue
        );
    }

    // 지출 목표 달성 여부 체크
    public ExpenseGoalResponseDTO checkExpenseGoal(Long memberId, YearMonth goalMonth) {
        log.info("Checking expense goal for memberId: {}, goalMonth: {}", memberId, goalMonth);
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        log.info("Fetched businessRegistration for memberId: {}, businessId: {}", memberId, business.getBusinessRegistrationId());

        QGoals qGoals = QGoals.goals;

        Goals currentGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                .and(qGoals.goalMonth.eq(goalMonth))
                )
                .fetchOne();

        BigDecimal currentMonthExpense = accountService.calculateTotalExpenses(goalMonth, memberId);
        log.info("Current month expense for memberId: {}, month: {}, expense: {}", memberId, goalMonth, currentMonthExpense);

        YearMonth oneMonthAgo = goalMonth.minusMonths(1);
        Goals oneMonthGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                .and(qGoals.goalMonth.eq(oneMonthAgo))
                )
                .fetchOne();

        YearMonth twoMonthsAgo = goalMonth.minusMonths(2);
        Goals twoMonthGoal = queryFactory
                .selectFrom(qGoals)
                .where(
                        qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                .and(qGoals.goalMonth.eq(twoMonthsAgo))
                )
                .fetchOne();

        return new ExpenseGoalResponseDTO(
                currentGoal != null ? currentGoal.getGoalMonth() : YearMonth.now(),
                twoMonthGoal != null ? twoMonthGoal.getExpenseGoal() : BigDecimal.ZERO,
                BigDecimal.ZERO,
                oneMonthGoal != null ? oneMonthGoal.getExpenseGoal() : BigDecimal.ZERO,
                BigDecimal.ZERO,
                currentGoal != null ? currentGoal.getExpenseGoal() : BigDecimal.ZERO,
                currentMonthExpense
        );
    }

    // 연간 목표 조회
    public List<GoalYearlyResponseDTO> getYearlyGoals(Long memberId, Year goalYear) {
        log.info("Fetching yearly goals for memberId: {}, goalYear: {}", memberId, goalYear);
        BusinessRegistration business = businessService.getBusinessIdByMemberID(memberId);
        log.info("Fetched businessRegistration for memberId: {}, businessId: {}", memberId, business.getBusinessRegistrationId());

        QGoals qGoals = QGoals.goals;

        List<GoalYearlyResponseDTO> yearlyGoals = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth goalMonth = YearMonth.of(goalYear.getValue(), month);
            Goals revenueGoal = queryFactory
                    .selectFrom(qGoals)
                    .where(
                            qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                    .and(qGoals.goalMonth.eq(goalMonth))
                    )
                    .fetchOne();

            BigDecimal realRevenue = posService.calculateMonthlyRevenue(memberId, goalMonth);

            Goals expenseGoal = queryFactory
                    .selectFrom(qGoals)
                    .where(
                            qGoals.businessRegistration.businessRegistrationId.eq(business.getBusinessRegistrationId())
                                    .and(qGoals.goalMonth.eq(goalMonth))
                    )
                    .fetchOne();

            BigDecimal realExpense = accountService.calculateTotalExpenses(goalMonth, memberId);

            GoalYearlyResponseDTO goalResponse = new GoalYearlyResponseDTO(
                    month,
                    revenueGoal != null ? revenueGoal.getRevenueGoal() : BigDecimal.ZERO,
                    realRevenue != null ? realRevenue : BigDecimal.ZERO,
                    expenseGoal != null ? expenseGoal.getExpenseGoal() : BigDecimal.ZERO,
                    realExpense != null ? realExpense : BigDecimal.ZERO
            );

            yearlyGoals.add(goalResponse);
            log.info("Added yearly goal for month: {}, goalResponse: {}", month, goalResponse);
        }

        return yearlyGoals;
    }
}
