package com.example.backend.controller;

import com.example.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    /// 1. 경제 지표 활용 시장 동향 보고서 생성
    @GetMapping("/market-trend")
    public ResponseEntity<Map<String, Object>> getMarketReport() {
        try {
            Map<String, Object> report = reportService.generateMarketReport();
            return ResponseEntity.ok(report);

        } catch (Exception e) {
            log.error("시장 동향 생성 실패", e);
            return null;
        }
    }

    /// 2. 동종 업계 비교 분석 보고서 생성 (지역 기반)
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
