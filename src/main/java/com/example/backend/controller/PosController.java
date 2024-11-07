package com.example.backend.controller;

import com.example.backend.dto.pos.MonthlySalesSummaryDTO;
import com.example.backend.service.PosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
@Slf4j
public class PosController {

    private final PosService posService;

    // 월 매출 및 해당 월의 일별 매출 반환
    @GetMapping("/{posId}/monthly-sales-summary")
    public ResponseEntity<MonthlySalesSummaryDTO> getMonthlySalesSummary(
            @PathVariable Long posId,
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        MonthlySalesSummaryDTO salesSummary = posService.getMonthlySalesSummary(memberId, posId, YearMonth.parse(month));
        return ResponseEntity.ok(salesSummary);
    }
}
