package com.example.backend.service;

import com.example.backend.dto.account.ExpenseDetailDTO;
import com.example.backend.dto.pos.MonthlyIncomeDTO;
import com.example.backend.model.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final PosService posService;
    private final AccountService accountService;
    private final JPAQueryFactory queryFactory;

    @Value("${OPENAI_API_KEY}")
    private String openAiApiKey;

    private WebClient webClient;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1";

    // 생성자에서 WebClient를 초기화합니다.
    @PostConstruct
    public void init() {
        log.info("API 키 확인: {}", openAiApiKey);

        if (openAiApiKey == null || openAiApiKey.isEmpty()) {
            log.error("API 키가 설정되지 않았습니다.");
            throw new IllegalStateException("API 키가 설정되지 않았습니다.");
        }

        this.webClient = WebClient.builder()
                .baseUrl(OPENAI_API_URL)
                .defaultHeader("Authorization", "Bearer " + openAiApiKey.trim())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    //////////////////// 1. 경제 지표 활용 시장 동향 보고서 생성
    public String generateMarketReport() {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 카페를 운영하는 사장님을 위한 시장 동향 보고서를 작성하는 AI입니다."),
                        Map.of("role", "user", "content", "**커피 원두, 우유, 설탕 등 주요 재료 가격 변동**과 이를 포함한 **환율 및 소비자 물가** 동향을 정리하고, **소비자 외식 심리**, **카페 소비 트렌드**를 포함해 다음과 같은 형식으로 이번 달 경제 지표를 분석하고, 카페 운영자가 참고할 수 있는 시장 동향을 JSON 형식으로 정리해주세요..\n\n- month : (이번달 숫자)\n- BSI_index : (실제 이번달 BSI 지수)\n- BSI_description : (이번달 BSI 지수에 대한 설명)\n- price_index : (환율 및 소비자 물가에 대한 설명)\n- food_trend : (외식업 소비 동향에 대한 설명)")
                        )
        );

        try {
            return webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("OpenAI API 호출 오류: {}", e.getResponseBodyAsString(), e);
            return "API 호출 오류 발생";
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생", e);
            return "예상치 못한 오류 발생";
        }
    }

    /////////////////////// 2. 동종 업계 비교 분석 보고서 생성 (지역 기반)
    public String generateIndustryComparisonReport(Long memberId, YearMonth month) {
        try {
            Map<String, Object> monthlyIncome = posService.calculateAverageMonthlyMetrics(month);
            Map<String, Object> categoryExpense = accountService.getAccountHistoryByRegion(memberId, month);
            MonthlyIncomeDTO myIncome  = posService.getMonthlyIncomeSummary(memberId, month);
            List<ExpenseDetailDTO.ExpenseDetail> myExpense = accountService.getExpenseDetails(month,memberId);


            String content = String.format("""
                다음 데이터는 평균 카페 운영 관련 지출 및 매출 데이터입니다.
                - 평균 매출 정보: %s
                - 평균 지출 정보: %s
                
                다음 데이터는 나의 카페 운영 관련 지출 및 매출 데이터입니다.
                - 평균 나의 매출 정보: %s
                - 평균 나의 지출 정보: %s
                """, monthlyIncome, categoryExpense, myIncome, myExpense
                    );

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(
                            Map.of("role", "system", "content", "당신은 카페를 운영하는 사장님을 위한 시장 동향 보고서를 작성하는 AI입니다. 다음 데이터는 카페 운영 관련 지출 및 매출 데이터입니다. 이를 바탕으로 동종 업계와 비교하여 분석 결과를 JSON 형식으로 제공해주세요.\n\n- accountHistory: (지출 데이터)\n- posSales: (매출 데이터)\n- businessRegistration: (사업장 주소를 기준으로 같은 동에 있는 데이터 비교)\n\n응답 형식은 아래와 같이 해주세요:\n\n- average_sale: (약 ~ 만원 형식으로 된 매출 평균)\n- average_expense: (약 ~ 만원 형식으로 된 지출 평균)\n- sale_description: (주변 카페와 비교한 카테고리 별 지출 분석 결과. 예시: '주변 카페 평균보다 임대료 관련 지출이 높아요!')\n- expense_description: (주변 카페와 비교한 시간과 매출 타입(카드/현금) 분석 결과. 예시: '주변 카페들은 카드 거래가 대부분이고, 주로 아침 시간대에 매출이 높아요!'"),
                            Map.of("role", "user", "content", content)
                    )
            );

            return webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (WebClientResponseException e) {
            log.error("OpenAI API 호출 오류: {}", e.getResponseBodyAsString(), e);
            return "API 호출 오류 발생";
        } catch (Exception e) {
            log.error("보고서 생성 중 예외 발생", e);
            return "보고서 생성 중 오류 발생";
        }
    }
}
