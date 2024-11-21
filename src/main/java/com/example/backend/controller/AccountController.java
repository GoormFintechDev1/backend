package com.example.backend.controller;

import com.example.backend.dto.account.*;
import com.example.backend.service.AccountService;
import com.example.backend.service.CardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final CardService cardService;

    // 지출 간단 보기
    @GetMapping("/expense")
    public ResponseEntity<ExpenseDTO> expense(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        ExpenseDTO expenseSummary = accountService.showSimpleExpense(memberId, yearMonth);
        
        List<Map<String, Object>> recommends = cardService.recommendCards(yearMonth, memberId);
        
        log.info("할인정보:: " + recommends);
        
        return ResponseEntity.ok(expenseSummary);
    }

    // 지출 상세보기
    @GetMapping("/expense/detail")
    public ResponseEntity<ExpenseDetailDTO> expenseDetail(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        ExpenseDetailDTO expenseDetails = accountService.showDetailExpense(memberId, yearMonth);
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
    public ResponseEntity<ProfitDetailDTO> profitDetail(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId){
        YearMonth yearMonth = YearMonth.parse(month);
        ProfitDetailDTO profitDetail = accountService.showProfitDetail(memberId, yearMonth);
        return ResponseEntity.ok(profitDetail);
    }

    // 주차별 지출 (월요일 기준으로 주차 시작)
    @GetMapping("/expense/week")
    public ResponseEntity<ExpenseWeekDTO> expenseWeek(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId
    ) {
        YearMonth yearMonth = YearMonth.parse(month);
        ExpenseWeekDTO expense = accountService.showWeekExpense(memberId, yearMonth);
        return ResponseEntity.ok(expense);
    }

    /////// 수기 입력
    // 입력
    @PostMapping("/create")
    public ResponseEntity<String> createAccount(
            @RequestBody CreateAccountHistoryDTO accountHistoryDTO,
            @AuthenticationPrincipal Long memberId) {
        Long historyId = accountService.createAccountHistory(memberId,accountHistoryDTO);
        String responseMessage = "생성 성공 || historyId : " + historyId;
        return ResponseEntity.ok(responseMessage);
    }

    // 수정
    @PutMapping("{historyId}/edit")
    public ResponseEntity<String> editAccount(
            @PathVariable Long historyId,
            @RequestBody CreateAccountHistoryDTO accountHistoryDTO){
        CreateAccountHistoryDTO updatedAccount = accountService.editAccountHistory(historyId,accountHistoryDTO);
        if (updatedAccount != null) {
            return ResponseEntity.ok("수정 성공");
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    // 삭제
    @DeleteMapping("{historyId}/delete")
    public ResponseEntity<String> deleteAccount(
            @PathVariable Long historyId){
        boolean deleted = accountService.deleteAccountHistory(historyId);
        if (deleted) {
            return ResponseEntity.ok("삭제 성공");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
