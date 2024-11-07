package com.example.backend.controller;

import com.example.backend.dto.account.expenseDTO;
import com.example.backend.dto.account.expenseDetailDTO;
import com.example.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    // 지출 간단 보기
    @GetMapping("/{accountId}/expense")
    public ResponseEntity<expenseDTO> expense(
            @PathVariable Long accountId,
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        expenseDTO expenseSummary = accountService.showSimpleExpense(memberId, accountId, yearMonth);
        return ResponseEntity.ok(expenseSummary);
    }

    // 지출 상세보기
    @GetMapping("/{accountId}/expense/detail")
    public ResponseEntity<expenseDetailDTO> expenseDetail(
            @PathVariable Long accountId,
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        expenseDetailDTO expenseDetails = accountService.showDetailExpense(memberId,accountId, yearMonth);
        return ResponseEntity.ok(expenseDetails);
    }
}
