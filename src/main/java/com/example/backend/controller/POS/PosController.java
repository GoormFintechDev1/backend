package com.example.backend.controller.POS;

import com.example.backend.dto.pos.DailyIncomeDTO;
import com.example.backend.dto.pos.IncomeHistoryDTO;
import com.example.backend.dto.pos.MonthlyIncomeDTO;
import com.example.backend.service.POS.PosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "포스", description = "포스 API")
public class PosController {

    private final PosService posService;


    @Operation(summary = "월 매출 세부 정보 조회", description = "특정 월의 매출 요약 및 일자별 매출 리스트를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월 매출 세부 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/monthly-income")
    public ResponseEntity<MonthlyIncomeDTO> getMonthlySalesSummary(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        MonthlyIncomeDTO incomeSummary = posService.getMonthlyIncomeSummary(memberId, YearMonth.parse(month));
        return ResponseEntity.ok(incomeSummary);
    }

    @Operation(summary = "일 매출 세부 정보 조회", description = "특정 날짜의 매출 세부 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일 매출 세부 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/daily-income")
    public ResponseEntity<DailyIncomeDTO> getDailyIncomeDetail(
            @RequestParam("date") String date,
            @AuthenticationPrincipal Long memberId) {
        DailyIncomeDTO incomeDetail = posService.getDailyIncomeDetail(memberId, LocalDate.parse(date));
        return ResponseEntity.ok(incomeDetail);
    }


    @Operation(summary = "매출 이력 조회", description = "3개월 치 월 매출 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매출 이력 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
   @GetMapping("/income-history")
    public ResponseEntity<IncomeHistoryDTO> getIncomeHistory(
           @RequestParam("month") String month,
           @AuthenticationPrincipal Long memberId) {
       IncomeHistoryDTO incomeHistory = posService.getIncomeHistory(memberId, YearMonth.parse(month));
       return ResponseEntity.ok(incomeHistory);
   }

}
