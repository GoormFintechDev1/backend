/*
package com.example.backend.controller.POS;

import com.example.backend.dto.pos.DailyIncomeDTO;
import com.example.backend.dto.pos.IncomeHistoryDTO;
import com.example.backend.dto.pos.MonthlyIncomeDTO;
import com.example.backend.service.POS.PosService;
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
public class PosController {

    private final PosService posService;

    // 월 매출 세부 정보 및 일자별 매출 리스트
    @GetMapping("/monthly-income")
    public ResponseEntity<MonthlyIncomeDTO> getMonthlySalesSummary(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        MonthlyIncomeDTO incomeSummary = posService.getMonthlyIncomeSummary(memberId, YearMonth.parse(month));
        return ResponseEntity.ok(incomeSummary);
    }

    // 특정 일 매출 세부 정보 반환
    @GetMapping("/daily-income")
    public ResponseEntity<DailyIncomeDTO> getDailyIncomeDetail(
            @RequestParam("date") String date,
            @AuthenticationPrincipal Long memberId) {
        DailyIncomeDTO incomeDetail = posService.getDailyIncomeDetail(memberId, LocalDate.parse(date));
        return ResponseEntity.ok(incomeDetail);
    }

    // 이번 달 매출 및 지난 2개월의 월 매출 정보
   @GetMapping("/income-history")
    public ResponseEntity<IncomeHistoryDTO> getIncomeHistory(
           @RequestParam("month") String month,
           @AuthenticationPrincipal Long memberId) {
       IncomeHistoryDTO incomeHistory = posService.getIncomeHistory(memberId, YearMonth.parse(month));
       return ResponseEntity.ok(incomeHistory);
   }

}
*/
