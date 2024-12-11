package com.example.backend.controller.BANK;

import com.example.backend.dto.account.*;
import com.example.backend.model.BANK.Account;
import com.example.backend.model.BANK.AccountHistory;
import com.example.backend.service.BANK.AccountService;
import com.example.backend.service.CardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "계좌", description = "계좌 API")
public class AccountController {

    private final AccountService accountService;
    private final CardService cardService;


    @Operation(summary = "지출 간단 보기", description = "월별로 사용자의 간단한 지출 요약 정보를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지출 요약 정보를 성공적으로 반환했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
    })
    @GetMapping("/expense")
    public ResponseEntity<ExpenseDTO> expense(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        ExpenseDTO expenseSummary = accountService.showSimpleExpense(memberId, yearMonth);

        return ResponseEntity.ok(expenseSummary);
    }

    @Operation(summary = "지출 상세보기", description = "월별로 사용자의 상세 지출 정보를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지출 상세 정보를 성공적으로 반환했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
    })
    @GetMapping("/expense/detail")
    public ResponseEntity<ExpenseDetailDTO> expenseDetail(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        ExpenseDetailDTO expenseDetails = accountService.showDetailExpense(memberId, yearMonth);
        return ResponseEntity.ok(expenseDetails);
    }


    @Operation(summary = "순 이익 보기", description = "월별로 사용자의 순 이익(총수익 - 총지출)을 계산하여 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "순 이익 정보를 성공적으로 반환했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
    })
    @GetMapping("/profit")
    public ResponseEntity<BigDecimal> netProfit(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {  // JWT에서 추출한 memberId
        YearMonth yearMonth = YearMonth.parse(month);
        BigDecimal netProfit = accountService.showNetProfit(memberId, yearMonth);
        return ResponseEntity.ok(netProfit);
    }

    // 순이익 상세 (총 수익, 매출 원가, 운영 비용, 세금, 순 이익)
    @Operation(summary = "순 이익 상세 보기", description = "월별로 사용자의 순 이익 상세 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "순 이익 상세 정보를 성공적으로 반환했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
    })
    @GetMapping("/profit/detail")
    public ResponseEntity<ProfitDetailDTO> profitDetail(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId){
        YearMonth yearMonth = YearMonth.parse(month);
        ProfitDetailDTO profitDetail = accountService.showProfitDetail(memberId, yearMonth);
        return ResponseEntity.ok(profitDetail);
    }


    @Operation(summary = "주차별 지출 보기", description = "월별로 사용자의 주차별 지출 정보를 반환합니다. (월요일 기준 주차 시작)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주차별 지출 정보를 성공적으로 반환했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.")
    })
    @GetMapping("/expense/week")
    public ResponseEntity<ExpenseWeekDTO> expenseWeek(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId
    ) {
        YearMonth yearMonth = YearMonth.parse(month);
        ExpenseWeekDTO expense = accountService.showWeekExpense(memberId, yearMonth);
        return ResponseEntity.ok(expense);
    }


}
