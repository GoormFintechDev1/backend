package com.example.backend.controller.BUSINESS;

import com.example.backend.service.BUSINESS.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/report")
@Tag(name = "레포트", description = "레포트 API")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "시장 동향 보고서 생성", description = "경제 지표를 활용하여 시장 동향 보고서를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시장 동향 보고서 생성 성공"),
            @ApiResponse(responseCode = "500", description = "시장 동향 보고서 생성 중 서버 오류 발생")
    })
    @GetMapping("/market-trend")
    public ResponseEntity<Map<String, Object>> getMarketReport(@RequestParam YearMonth month) {
        try {
            Map<String, Object> report = reportService.generateMarketReport(month);
            return ResponseEntity.ok(report);

        } catch (Exception e) {
            log.error("시장 동향 생성 실패", e);
            return null;
        }
    }

    @Operation(summary = "동종 업계 비교 분석 보고서 생성", description = "지역 기반으로 동종 업계 비교 분석 보고서를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "동종 업계 비교 분석 보고서 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/industry-comparison")
    public ResponseEntity<Map<String, Object>> getIndustryComparison(
            @AuthenticationPrincipal Long memberId,  // JWT에서 추출한 memberId
            @RequestParam YearMonth month

    ) {
        try {
            Map<String, Object> report = reportService.generateIndustryComparisonReport(memberId, month);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("동종 업계 비교 분석 실패", e);
            return null;
        }
    }

    @Operation(summary = "모든 보고서 조회", description = "특정 월에 대한 모든 보고서를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모든 보고서 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllReport(
            @AuthenticationPrincipal Long memberId,
            @RequestParam YearMonth month
    ) {
        try {
            Map<String, Map<String, Object>> reports = reportService.getAllReports(memberId, month);
            return ResponseEntity.ok(Map.of("reports", reports));
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.<String, Object>of("error", "잘못된 요청: " + e.getMessage()));
        } catch (Exception e) {
            log.error("보고서 생성 오류", e);
            return ResponseEntity.status(500).body(Map.of("error", "보고서 생성 중 오류 발생"));
        }
    }

    @Operation(summary = "지난 달 보고서 존재 여부 확인", description = "사용자의 지난 달 보고서가 존재하는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지난 달 보고서 존재 여부 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/previous-month/check")
    public ResponseEntity<Boolean> checkPreviousMonthReports(
            @AuthenticationPrincipal Long memberId // JWT에서 추출한 memberId
    ) {
        try {
            // 전 달 리포트 존재 여부 확인
            boolean reportsAvailable = reportService.previousMonthReportChecker(memberId);
            return ResponseEntity.ok(reportsAvailable);
        } catch (IllegalArgumentException e) {
            log.error("리포트 확인 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            log.error("리포트 확인 중 서버 오류", e);
            return ResponseEntity.status(500).body(false);
        }

    }
}
