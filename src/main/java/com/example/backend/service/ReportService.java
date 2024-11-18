package com.example.backend.service;

import com.example.backend.dto.account.ExpenseDetailDTO;
import com.example.backend.dto.pos.MonthlyIncomeDTO;
import com.example.backend.model.Member;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;


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
    private final ObjectMapper objectMapper;

    public Map<String, Object> generateMarketReport() {
        // 실제 최신 데이터를 가져왔다고 가정한 예시입니다.
        String latestExchangeRate = "1,320원";  // 실제 API 호출로 가져올 수 있습니다.
        String bsiIndex = "98";  // 실제 경제 지표 API로부터 가져올 수 있습니다.
        String priceDescription = "원두 가격 5% 상승, 우유 가격 3% 상승, 설탕 가격 안정적";  // 실제 API에서 가져온 데이터 예시

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-2024-08-06",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 2024년 11월 15일 한국의 경제 뉴스를 기반으로 카페 운영자를 위한 시장 동향 보고서를 작성하는 AI입니다. " +
                                "실제 최신 뉴스와 공공 데이터(예: 한국은행, 기상청, 농림축산식품부, 통계청 등)를 기반으로 보고서를 작성하세요. 20대의 친근한 여성처럼 대답하세요." +
                                "2024년 11월 15일 기준 환율은 1400.90원 이고, BSI 지수는 91.8입니다. 모든 정보에 대한 실제 출처를 적어주세요. 출처가 무조건 있어야 합니다. 출처가 없는 값은 보여주지 마세요" ),

                        Map.of("role", "user", "content", String.format("""
                                다음 정보를 JSON 형식으로 정리해주세요:
                                
                                1. **month**: 2024년 11월
                                2. **BSI_index**: %s
                                3. **BSI_description**: %s의 BSI 지수를 기준으로 한 기업들의 경기 전망 설명 (실제 데이터에 근거)
                                4. **exchange_rate**: %s 기준 원/달러 환율의 변화와 그로 인해 예상되는 카페 운영에 미칠 영향
                                5. **price_index**: %s (원두, 우유, 설탕 등 주요 카페 재료 가격 변동에 따른 카페 운영 예상 영향)
                                6. **food_trend**: 한국 내 최신 카페 소비 트렌드 (뉴스나 시장 조사에 근거)
                                7. **recommendations**: 위 정보들을 바탕으로 카페 운영자를 위한 권장 사항 (실제 데이터를 기반으로 예측)
                                
                                모든 정보는 가능한 한 정확한 최신 자료에 기반하여 작성하며, 허구적인 데이터는 포함하지 마세요.
                                """, bsiIndex, bsiIndex, latestExchangeRate, priceDescription))
                ),
                "functions", List.of(
                        Map.of(
                                "name", "generateMarketReport",
                                "description", "주요 재료 가격 변동 및 카페 음료, 디저트 소비 동향을 요약한 시장 동향 월간 리포트를 생성합니다. 모든 정보는 한국의 최신 뉴스와 공공 데이터를 기반으로 생성하세요.",
                                "parameters", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "month", Map.of("type", "integer", "description", "2024년 11월"),
                                                "BSI_index", Map.of("type", "integer", "description", "2024년 11월 15일 기준 최신 기업경기실사지수 (BSI)"),
                                                "BSI_description", Map.of("type", "string", "description", "오늘 기준 BSI 지수에 대한 설명"),
                                                "exchange_rate", Map.of("type", "string", "description", "2024년 11월 15일 기준 환율 정보와 그로 인한 카페 운영 예상 영향"),
                                                "price_index", Map.of("type", "string", "description", "주요 카페 재료의 가격 변동 (우유, 원두, 설탕 등)"),
                                                "food_trend", Map.of("type", "string", "description", "한국 내 최신 카페 소비 동향"),
                                                "recommendations", Map.of(
                                                        "type", "array",
                                                        "items", Map.of("type", "string"),
                                                        "description", "위 정보에 기반하여 카페 운영자를 위한 예측 및 권장 사항 목록"
                                                )
                                        ),
                                        "required", List.of("month", "BSI_index", "BSI_description", "price_index", "food_trend", "recommendations")
                                )
                        )
                ),
                "function_call", Map.of("name", "generateMarketReport")
        );

        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // JSON 응답 파싱
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode argumentsNode = rootNode.path("choices").get(0).path("message").path("function_call").path("arguments");

            // argumentsNode가 문자열이므로 다시 JSON으로 파싱
            String argumentsJson = argumentsNode.asText();
            JsonNode parsedArguments = objectMapper.readTree(argumentsJson);

            // JsonNode를 Map으로 변환
            Map<String, Object> marketReport = objectMapper.convertValue(parsedArguments, Map.class);

            return marketReport;

        } catch (WebClientResponseException e) {
            System.err.println("API 호출 오류: " + e.getResponseBodyAsString());
            return Map.of("error", "API 호출 오류 발생");
        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            return Map.of("error", "예상치 못한 오류 발생");
        }
    }

    /////////////////////// 2. 동종 업계 비교 분석 보고서 생성 (지역 기반)
    public Map<String,Object> generateIndustryComparisonReport(Long memberId, YearMonth month) {

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
                    "model", "gpt-4o-2024-08-06",
                    "messages", List.of(
                            Map.of("role", "system", "content", "당신은 카페를 운영하는 사장님을 위한 시장 동향 보고서를 작성하는 AI입니다." +
                                    " 다음 데이터는 카페 운영 관련 지출 및 매출 데이터입니다. " + content +
                                    "이를 바탕으로 동종 업계와 비교하여 분석 결과를 JSON 형식으로 제공해주세요." + " 20대의 친근한 여성처럼 대답하세요."),
                            Map.of("role", "user", "content", String.format("""
                                    다음 정보를 JSON 형식으로 정리해주세요 : 
                              
                                    1. **average_sale** : 약 ~ 만원 형식으로 된 매출 평균
                                    2. **average_expense**: 약 ~ 만원 형식으로 된 지출 평균
                                    3. **sale_description**: 주변 카페와 비교한 카테고리 별 지출 분석 결과. (예시: '주변 카페 평균보다 임대료 관련 지출이 높아요!')
                                    4. **expense_description**: 주변 카페와 비교한 시간과 매출 타입(카드/현금) 분석 결과 (예시: '주변 카페들은 카드 거래가 대부분이고, 주로 아침 시간대에 매출이 높아요!'"),
                      
                                    """))
                    ),
                    "functions", List.of(
                            Map.of(
                                    "name", "generateIndustryComparisonReport",
                                    "description", "지출과 매출에 대한 비교 분석 리포트를 생성합니다.",
                                    "parameters", Map.of(
                                            "type", "object",
                                            "properties", Map.of(
                                                "average_sale", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 매출 평균"),
                                                "average_expense", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 지출 평균"),
                                                "sale_description", Map.of("type", "string", "description", "주변 카페와 비교한 카테고리 별 지출 분석 결과"),
                                                "expense_description", Map.of("type", "string", "description", "주변 카페와 비교한 시간과 매출 타입(카드/현금) 분석 결과")
                                            )
                                    ),
                                    "required", List.of("average_sale", "average_expense","sale_description","expense_description")
                            )
                    ),
                    "function_call", Map.of("name", "generateIndustryComparisonReport")

            );


        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // JSON 응답 파싱
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode argumentsNode = rootNode.path("choices").get(0).path("message").path("function_call").path("arguments");

            // argumentsNode가 문자열이므로 다시 JSON으로 파싱
            String argumentsJson = argumentsNode.asText();
            JsonNode parsedArguments = objectMapper.readTree(argumentsJson);

            // JsonNode를 Map으로 변환
            Map<String, Object> marketReport = objectMapper.convertValue(parsedArguments, Map.class);

            return marketReport;

        } catch (WebClientResponseException e) {
            System.err.println("API 호출 오류: " + e.getResponseBodyAsString());
            return Map.of("error", "API 호출 오류 발생");
        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            return Map.of("error", "예상치 못한 오류 발생");
        }
    }
}
