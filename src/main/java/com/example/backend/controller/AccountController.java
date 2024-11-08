package com.example.backend.controller;

import com.example.backend.dto.account.expenseDTO;
import com.example.backend.dto.account.expenseDetailDTO;
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

}
