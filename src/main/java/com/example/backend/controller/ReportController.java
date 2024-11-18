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
            Map<String, Object> report  = reportService.generateIndustryComparisonReport(memberId, month);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("동종 업계 비교 분석 실패", e);
            return null;
        }
    }
}
