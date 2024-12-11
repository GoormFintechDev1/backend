package com.example.backend.controller;

import com.example.backend.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "카드", description = "카드 API")
public class CardController {
    private final CardService cardService;

    @Operation(summary = "카드 추천 조회", description = "사용자의 월별 소비 패턴을 기반으로 카드를 추천합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카드 추천 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/recommend")
    public ResponseEntity<List<Map<String, Object>>> cardRecommend(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {
        YearMonth yearMonth = YearMonth.parse(month);

        List<Map<String, Object>> recommends = cardService.recommendCards(yearMonth, memberId);

        return ResponseEntity.ok(recommends);
    }
}