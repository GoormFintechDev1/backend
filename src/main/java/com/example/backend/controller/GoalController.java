package com.example.backend.controller;

import com.example.backend.dto.goals.*;
import com.example.backend.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

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

//    // 매출 목표 설정
//    @PostMapping("/set/revenue")
//    public ResponseEntity<String> setRevenueGoal(@AuthenticationPrincipal Long memberId, @RequestBody RevenueGoalRequestDTO requestDTO) {
//        goalService.setRevenueGoal(memberId, requestDTO);
//        return ResponseEntity.ok("매출 목표 설정 완료!");
//    }
//
//    // 지출 목표 설정
//    @PostMapping("/set/expense")
//    public ResponseEntity<String> setExpenseGoal(@AuthenticationPrincipal Long memberId, @RequestBody ExpenseGoalRequestDTO requestDTO) {
//        goalService.setExpenseGoal(memberId, requestDTO);
//        return ResponseEntity.ok("지출 목표 설정 완료!");
//    }

    // 목표 업데이트
    @PutMapping("/update")
    public ResponseEntity<String> updateGoal(@AuthenticationPrincipal Long memberId,@RequestBody GoalRequestDTO goalRequestDTO) {
        goalService.updateGoal(memberId, goalRequestDTO);
        return ResponseEntity.ok("목표 재설정 완료!");
    }
//
//    @PutMapping("/update/revenue")
//    public ResponseEntity<String> updateRevenueGoal(@AuthenticationPrincipal Long memberId, @RequestBody RevenueGoalRequestDTO requestDTO) {
//        goalService.updateRevenueGoal(memberId, requestDTO);
//        return ResponseEntity.ok("매출 목표 재설정 완료!");
//    }
//
//    // 목표 업데이트
//    @PutMapping("/update/expense")
//    public ResponseEntity<String> updateExpenseGoal(@AuthenticationPrincipal Long memberId, @RequestBody ExpenseGoalRequestDTO requestDTO) {
//        goalService.updateExpenseGoal(memberId, requestDTO);
//        return ResponseEntity.ok("지출 목표 재설정 완료!");
//    }

    // 목표 달성 여부 확인
    @GetMapping("/check/revenue")
    public ResponseEntity<RevenueGoalResponseDTO> checkRevenueGoal(@AuthenticationPrincipal Long memberId, @RequestParam YearMonth goalMonth) {
        RevenueGoalResponseDTO responseDTO = goalService.checkRevenueGoal(memberId, goalMonth);
        return ResponseEntity.ok(responseDTO);
    }

    // 목표 달성 여부 확인
    @GetMapping("/check/expense")
    public ResponseEntity<ExpenseGoalResponseDTO> checkExpenseGoal(@AuthenticationPrincipal Long memberId, @RequestParam YearMonth goalMonth) {
        ExpenseGoalResponseDTO responseDTO = goalService.checkExpenseGoal(memberId, goalMonth);
        return ResponseEntity.ok(responseDTO);
    }

}
