package com.example.backend.controller;

import com.example.backend.service.CardService;
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
public class CardController {
    private final CardService cardService;

    // 지출 간단 보기
    @GetMapping("/recommend")
    public ResponseEntity<List<Map<String, Object>>> cardRecommend(
            @RequestParam("month") String month,
            @AuthenticationPrincipal Long memberId) {
        YearMonth yearMonth = YearMonth.parse(month);

        List<Map<String, Object>> recommends = cardService.recommendCards(yearMonth, memberId);

        return ResponseEntity.ok(recommends);
    }
}