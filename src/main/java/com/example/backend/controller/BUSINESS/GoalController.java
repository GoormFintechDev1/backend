package com.example.backend.controller.BUSINESS;

import com.example.backend.dto.goals.*;
import com.example.backend.service.BUSINESS.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "목표", description = "목표 API")
public class GoalController {

    private final GoalService goalService;

    @Operation(summary = "목표 설정", description = "사용자가 새로운 매출 또는 지출 목표를 설정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목표 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "goalMonth", description = "목표 월"),
            @Parameter(name = "revenueGoal", description = "매출 목표"),
            @Parameter(name = "expenseGoal", description = "지출 목표"),
    })
    @PostMapping("/set")
    public ResponseEntity<String> setGoal(@AuthenticationPrincipal Long memberId, @RequestBody GoalRequestDTO goalRequestDTO) {
        goalService.setGoal(memberId, goalRequestDTO);
        return ResponseEntity.ok("목표 설정 완료!");
    }

    @Operation(summary = "목표 업데이트", description = "사용자가 기존 목표를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목표 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameters({
            @Parameter(name = "goalMonth", description = "목표 월"),
            @Parameter(name = "revenueGoal", description = "매출 목표"),
            @Parameter(name = "expenseGoal", description = "지출 목표"),
    })
    @PutMapping("/update")
    public ResponseEntity<String> updateGoal(@AuthenticationPrincipal Long memberId,@RequestBody GoalRequestDTO goalRequestDTO) {
        goalService.updateGoal(memberId, goalRequestDTO);
        return ResponseEntity.ok("목표 재설정 완료!");
    }

    @Operation(summary = "지난 2개월 매출 목표 조회", description = "3개월 치 매출 목표를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매출 목표 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/check/revenue")
    public ResponseEntity<RevenueGoalResponseDTO> checkRevenueGoal(@AuthenticationPrincipal Long memberId, @RequestParam YearMonth goalMonth) {
        RevenueGoalResponseDTO responseDTO = goalService.checkRevenueGoal(memberId, goalMonth);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "지난 2개월 지출 목표 조회", description = "3개월 치 지출 목표를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지출 목표 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/check/expense")
    public ResponseEntity<ExpenseGoalResponseDTO> checkExpenseGoal(@AuthenticationPrincipal Long memberId, @RequestParam YearMonth goalMonth) {
        ExpenseGoalResponseDTO responseDTO = goalService.checkExpenseGoal(memberId, goalMonth);
        return ResponseEntity.ok(responseDTO);
    }


    @Operation(summary = "연간 목표 조회", description = "특정 연도의 월별 목표 데이터를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연간 목표 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/total")
    public ResponseEntity<List<GoalYearlyResponseDTO> > getMonthlyGoal(
            @AuthenticationPrincipal Long memberId,
            @RequestParam Year goalYear) {
        List<GoalYearlyResponseDTO> responseDTO = goalService.getYearlyGoals(memberId, goalYear);
        return ResponseEntity.ok(responseDTO);
    }

}
