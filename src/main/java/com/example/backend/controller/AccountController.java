package com.example.backend.controller;

import com.example.backend.dto.account.expenseDTO;
import com.example.backend.dto.account.expenseDetailDTO;
import com.example.backend.dto.account.expenseWeekDTO;
import com.example.backend.dto.account.profitDetailDTO;
import com.example.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    // 지출 간단 보기
    @GetMapping("/expense")
    public ResponseEntity<expenseDTO> expense(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        expenseDTO expenseSummary = accountService.showSimpleExpense(memberId, yearMonth);
        return ResponseEntity.ok(expenseSummary);
    }

    // 지출 상세보기
    @GetMapping("/expense/detail")
    public ResponseEntity<expenseDetailDTO> expenseDetail(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        expenseDetailDTO expenseDetails = accountService.showDetailExpense(memberId, yearMonth);
        return ResponseEntity.ok(expenseDetails);
    }

    // 순 이익 (총수익 - 총지출)
    @GetMapping("/profit")
    public ResponseEntity<BigDecimal> netProfit(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        BigDecimal netProfit = accountService.showNetProfit(memberId, yearMonth);
        return ResponseEntity.ok(netProfit);
    }

    // 순이익 상세 (총 수익, 매출 원가, 운영 비용, 세금, 순 이익)
    @GetMapping("/profit/detail")
    public ResponseEntity<profitDetailDTO> profitDetail(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId){
        YearMonth yearMonth = YearMonth.parse(month);
        profitDetailDTO profitDetail = accountService.showProfitDetail(memberId, yearMonth);
        return ResponseEntity.ok(profitDetail);
    }

    // 주차별 지출 (월요일 기준으로 주차 시작)
    @GetMapping("/expense/week")
    public ResponseEntity<expenseWeekDTO> expenseWeek(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId
    ) {
        YearMonth yearMonth = YearMonth.parse(month);
        expenseWeekDTO expense = accountService.showWeekExpense(memberId, yearMonth);
        return ResponseEntity.ok(expense);
    }

}
