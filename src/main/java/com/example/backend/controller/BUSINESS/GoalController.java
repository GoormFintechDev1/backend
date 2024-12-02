/*
package com.example.backend.controller.BUSINESS;

import com.example.backend.dto.goals.*;
import com.example.backend.service.BUSINESS.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/goal")
public class GoalController {

    private final GoalService goalService;

    // 목표 설정
    @PostMapping("/set")
    public ResponseEntity<String> setGoal(@AuthenticationPrincipal Long memberId, @RequestBody GoalRequestDTO goalRequestDTO) {
        goalService.setGoal(memberId, goalRequestDTO);
        return ResponseEntity.ok("목표 설정 완료!");
    }

    // 목표 업데이트
    @PutMapping("/update")
    public ResponseEntity<String> updateGoal(@AuthenticationPrincipal Long memberId,@RequestBody GoalRequestDTO goalRequestDTO) {
        goalService.updateGoal(memberId, goalRequestDTO);
        return ResponseEntity.ok("목표 재설정 완료!");
    }

    // 지난 2개월 매출 목표 조회
    @GetMapping("/check/revenue")
    public ResponseEntity<RevenueGoalResponseDTO> checkRevenueGoal(@AuthenticationPrincipal Long memberId, @RequestParam YearMonth goalMonth) {
        RevenueGoalResponseDTO responseDTO = goalService.checkRevenueGoal(memberId, goalMonth);
        return ResponseEntity.ok(responseDTO);
    }

    // 지난 2개월 지출 목표 조회
    @GetMapping("/check/expense")
    public ResponseEntity<ExpenseGoalResponseDTO> checkExpenseGoal(@AuthenticationPrincipal Long memberId, @RequestParam YearMonth goalMonth) {
        ExpenseGoalResponseDTO responseDTO = goalService.checkExpenseGoal(memberId, goalMonth);
        return ResponseEntity.ok(responseDTO);
    }


    ////// 연간 목표
    @GetMapping("/total")
    public ResponseEntity<List<GoalYearlyResponseDTO> > getMonthlyGoal(
            @AuthenticationPrincipal Long memberId,
            @RequestParam Year goalYear) {
        List<GoalYearlyResponseDTO> responseDTO = goalService.getYearlyGoals(memberId, goalYear);
        return ResponseEntity.ok(responseDTO);
    }

}
*/
