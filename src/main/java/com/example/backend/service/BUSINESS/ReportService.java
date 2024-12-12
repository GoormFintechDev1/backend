package com.example.backend.service.BUSINESS;

import com.example.backend.dto.account.ExpenseDTO;
import com.example.backend.dto.account.ExpenseDetailDTO;
import com.example.backend.dto.pos.MonthlyIncomeDTO;
import com.example.backend.model.BUSINESS.*;
import com.example.backend.service.BANK.AccountService;
import com.example.backend.service.POS.PosService;
import com.example.backend.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final RedisService redisService;
    private final BusinessService businessService;

    @PersistenceContext
    private EntityManager em;

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

    @Transactional
    public String getOrCreateReport(Long memberId, YearMonth month, String reportType) {


        BusinessRegistration businessRegistration = businessService.getBusinessIdByMemberID(memberId);

        if (businessRegistration == null) {
            throw new IllegalArgumentException("Member ID: " + memberId + "에 대한 BusinessRegistration이 존재하지 않습니다.");
        }

        LocalDate reportMonth = month.atDay(1);

        // 1. 리포트 조회
        Report existingReport = queryFactory.selectFrom(QReport.report)
                .where(
                        QReport.report.businessRegistration.businessRegistrationId.eq(businessRegistration.getBusinessRegistrationId()),
                        QReport.report.reportMonth.eq(reportMonth),
                        QReport.report.reportType.eq(reportType)
                )
                .fetchOne();

        // 2. 존재하면 JSON 반환
        if (existingReport != null) {
            return existingReport.getContent();
        }

        // 3. 리포트가 없으면 GPT API 호출 및 저장
        Map<String, Object> reportData = generateReportFromAPI(memberId, month, reportType);

        // API 호출이 성공적인지 검증
        if (reportData.containsKey("error")) {
            log.error("API 호출 오류로 리포트를 생성하지 못했습니다.{}", reportData.get("error"));
            throw new RuntimeException("리포트 생성 실패: " + reportData.get("error"));
        }

        saveReport(businessRegistration, reportMonth, reportType, reportData);


        try {
            String generatedContent = objectMapper.writeValueAsString(reportData); // JSON 직렬화
            log.info("생성된 리포트 반환: {}", generatedContent);
            return generatedContent;
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 오류: {}", e.getMessage());
            throw new RuntimeException("JSON 직렬화 중 오류 발생", e);
        }


    }

    @Transactional
    public Map<String, Map<String, Object>> getAllReports(Long memberId, YearMonth month) {
        Map<String, Map<String, Object>> reports = new HashMap<>();

        // MARKET_REPORT 가져오기
        String marketReport = getOrCreateReport(memberId, month, "MARKET_REPORT");
        reports.put("MARKET_REPORT", parseJson(marketReport));

        // INDUSTRY_REPORT 가져오기
        String industryReport = getOrCreateReport(memberId, month, "INDUSTRY_REPORT");
        reports.put("INDUSTRY_REPORT", parseJson(industryReport));
        return reports;
    }

    private Map<String, Object> generateReportFromAPI(Long memberId, YearMonth month, String reportType) {
        if ("MARKET_REPORT".equals(reportType)) {
            return generateMarketReport(month);
        } else if ("INDUSTRY_REPORT".equals(reportType)) {
            return generateIndustryComparisonReport(memberId, month);
        }
        throw new IllegalArgumentException("Invalid report type: " + reportType);
    }


    private Map<String, Object> parseJson(String json) {
        try {
            // 입력 JSON 문자열 확인
            if (json == null || json.isBlank()) {
                throw new IllegalArgumentException("JSON 데이터가 비어있습니다.");
            }
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 오류: " + e.getMessage(), e);
        }
    }

    public String getMarketIssueByMonth(YearMonth month) {
        QPrompt prompt = QPrompt.prompt;

        // "시장 이슈" 데이터 가져오기
        String marketIssues = queryFactory
                .select(prompt.contents)
                .from(prompt)
                .where(
                        prompt.month.eq(month),
                        prompt.type.eq("issue")
                )
                .fetchOne();

        return marketIssues != null ? marketIssues : "";
    }

    public String getTrendByMonth(YearMonth month) {
        QPrompt prompt = QPrompt.prompt;

        // "트렌드" 데이터 가져오기
        String trends = queryFactory
                .select(prompt.contents)
                .from(prompt)
                .where(
                        prompt.month.eq(month),
                        prompt.type.eq("trend")
                )
                .fetchOne();

        // "트렌드" 결과 합치기
        return trends != null ? trends : "";
    }


    @Transactional
    public void saveReport(BusinessRegistration businessRegistration, LocalDate reportMonth, String reportType, Map<String, Object> reportData) {
        try {
            String content = objectMapper.writeValueAsString(reportData);


            Report newReport = new Report();
            newReport.setBusinessRegistration(businessRegistration);
            newReport.setReportMonth(reportMonth);
            newReport.setReportType(reportType);
            newReport.setContent(content);

            em.persist(newReport);
//        em.flush();
        } catch (Exception e) {
            throw new RuntimeException("JSON 오류", e);
        }
    }

    //////////////////// 1. 경제 지표 활용 시장 동향 보고서 생성
    private final ObjectMapper objectMapper;
    @Transactional
    public Map<String, Object> generateMarketReport(YearMonth month) {
        Map<Integer, String> bsiData = Map.of(
                9, "9월 소상공인 전망 BSI 지수: 75.9 / 전월대비 +20.1p",
                10, "10월 소상공인 전망 BSI 지수: 85.9 / 전월대비 +10.0p",
                11, "11월 소상공인 전망 BSI 지수: 75.6 / 전월대비 -10.3p"
        );

        Map<Integer, String> cpiData = Map.of(
                9, "9월 CPI 지수: 114.65 / 전월비 0.1% 상승 / 전년동월비 1.6% 상승",
                10, "10월 CPI 지수: 114.69 / 전월비 0.1% 상승 / 전년동월비 1.3% 상승",
                11, "11월 CPI 지수: 114.40 / 전월비 -0.3% 하락 / 전년동월비 1.5% 상승"
        );

        Map<Integer, String> bsiIndexData = Map.of(
                9, "75.9",
                10, "85.9",
                11, "75.6"
        );

        Map<Integer, String> cpiIndexData = Map.of(
                9, "114.65",
                10, "114.69",
                11, "114.40"
        );

        int currentMonth = month.getMonthValue();

        String bsiDescription = bsiData.getOrDefault(currentMonth, "해당 월의 BSI 데이터가 없습니다.");
        String cpiDescription = cpiData.getOrDefault(currentMonth, "해당 월의 CPI 데이터가 없습니다.");
        String bsiIndex = bsiIndexData.getOrDefault(currentMonth, "데이터 없음");
        String cpiIndex = cpiIndexData.getOrDefault(currentMonth, "데이터 없음");

        String market = getMarketIssueByMonth(month);
        String trend = getTrendByMonth(month);

//        // 기본값을 추가하여 값이 없을 경우 대체 문구 제공
//        market = (market == null || market.isBlank()) ? "시장 이슈 데이터가 없습니다. 최신 이슈를 업데이트하세요." : market;
//        trend = (trend == null || trend.isBlank()) ? "트렌드 데이터가 없습니다. 최신 트렌드를 업데이트하세요." : trend;

        String content = String.format("""
            다음 데이터는 현재 월 기준으로 제공된 시장 동향 데이터입니다.
            - BSI 지수: %s
            - BSI 설명: %s
            - CPI 지수: %s
            - CPI 설명: %s
            - 시장 이슈: %s
            - 트렌드: %s
            """,
                bsiIndex, bsiDescription, cpiIndex, cpiDescription, market, trend
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "당신은 카페를 운영하는 사장님을 위한 시장 동향 보고서를 작성하는 AI입니다. " +
                                        "다음 데이터는 카페 운영 관련 시장 동향 정보입니다: ", +currentMonth, content +
                                        "이를 바탕으로 분석 결과와 권장 사항을 JSON 형식으로 제공해주세요. " +
                                        "답변은 친근한 아나운서처럼 작성해주세요. 귀하 보다는 사장님 표현을 써주세요. -하는게 좋을 것 같아요. - 했어요. -해요. -하는게 어떨까요? 등의 표현을 써주세요."
                        ),
                        Map.of("role", "user", "content",
                                """
                                다음 정보를 JSON 형식으로 정리하세요:
                                1. **month**: 해당 월
                                2. **BSI_index**: BSI 지수
                                3. **BSI_description**: BSI 지수에 대한 설명
                                4. **CPI_index**: CPI 지수
                                5. **CPI_description**: CPI 지수에 대한 설명
                                6. **market_issue**: 시장 이슈에 대한 설명
                                7. **trend**: 트렌드에 대한 설명
                                8. **recommendation**: 위 데이터를 기반으로 종합적으로 고려한 권장 사항
                                """
                        )
                ),
                "functions", List.of(
                        Map.of(
                                "name", "generateMarketReport",
                                "description", "BSI와 CPI 데이터를 기반으로 시장 동향 및 권장 사항을 제공합니다.",
                                "parameters", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "month", Map.of("type", "integer", "description", "2024년 해당 월"),
                                                "BSI_index", Map.of("type", "string", "description", "오늘 기준 BSI 지수"),
                                                "BSI_description", Map.of("type", "string", "description", "오늘 기준 BSI 지수에 대한 자세한 설명"),
                                                "CPI_index", Map.of("type", "string", "description", "오늘 기준 소비자물가지수 (CPI)"),
                                                "CPI_description", Map.of("type", "string", "description", "오늘 기준 소비자물가지수 (CPI)에 대한 자세한 설명"),
                                                "market_issue", Map.of("type", "string", "description", "시장 이슈에 대한 설명"),
                                                "trend", Map.of("type", "string", "description", "트렌드에 대한 설명"),
                                                "recommendation", Map.of("type", "string", "description", "위 정보를 종합적으로 고려한 권장 사항")
                                        ),
                                        "required", List.of("month", "BSI_index", "BSI_description", "CPI_index", "CPI_description", "market_issue", "trend", "recommendation")
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

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode argumentsNode = rootNode.path("choices").get(0).path("message").path("function_call").path("arguments");

            String argumentsJson = argumentsNode.asText();
            JsonNode parsedArguments = objectMapper.readTree(argumentsJson);

            return objectMapper.convertValue(parsedArguments, Map.class);

        } catch (WebClientResponseException e) {
            System.err.println("API 호출 오류: " + e.getResponseBodyAsString());
            return Map.of("error", "API 호출 오류 발생");
        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            return Map.of("error", "예상치 못한 오류 발생");
        }
    }


//    public Map<String, Object> generateMarketReport(YearMonth month) {
//        // BSI 및 CPI 데이터 정의
//        Map<Integer, String> bsiData = Map.of(
//                9, "9월 소상공인 전망 BSI 지수: 75.9 / 전월대비 +20.1p",
//                10, "10월 소상공인 전망 BSI 지수: 85.9 / 전월대비 +10.0p",
//                11, "11월 소상공인 전망 BSI 지수: 75.6 / 전월대비 -10.3p"
//        );
//
//        Map<Integer, String> cpiData = Map.of(
//                9, "9월 CPI 지수: 114.65 / 전월비 0.1% 상승 / 전년동월비 1.6% 상승",
//                10, "10월 CPI 지수: 114.69 / 전월비 0.1% 상승 / 전년동월비 1.3% 상승",
//                11, "11월 CPI 지수: 114.40 / 전월비 -0.3% 하락 / 전년동월비 1.5% 상승"
//        );
//
//        // 현재 월 가져오기
//        int currentMonth = month.getMonthValue();
//
//        // 월별 데이터 선택
//        String bsiDescription = bsiData.getOrDefault(currentMonth, "해당 월의 BSI 데이터가 없습니다.");
//        String cpiDescription = cpiData.getOrDefault(currentMonth, "해당 월의 CPI 데이터가 없습니다.");
//
//        // 기존 데이터 (변경 없음)
//        String market = getMarketIssueByMonth(month);
//        String trend = getTrendByMonth(month);
//
//        // 요청 바디 구성
//        Map<String, Object> requestBody = Map.of(
//                "model", "gpt-4o",
//                "messages", List.of(
//                        Map.of("role", "system", "content", String.format("당신은 %s 한국의 경제 뉴스를 기반으로 카페 운영자를 위한 시장 동향 보고서를 작성하는 AI입니다. " +
//                                "실제 최신 뉴스와 공공 데이터(예: 한국은행, 기상청, 농림축산식품부, 통계청 등)를 기반으로 작성하세요. " +
//                                "아나운서처럼 대답하세요.", currentMonth)),
//                        Map.of("role", "user", "content", String.format("""
//                            다음 정보를 JSON 형식으로 정리해주세요:
//
//                            1. **month**: %s
//                            2. **BSI_description**: %s
//                            3. **CPI_description**: %s
//                            4. **market_issue**: %s
//                            5. **trend**: %s
//                            6. **recommendations**: 위 정보들을 바탕으로 카페 운영자를 위한 권장 사항 (실제 데이터를 기반으로 예측)
//
//                            모든 정보는 가능한 한 정확한 최신 자료에 기반하여 작성하며, 허구적인 데이터는 포함하지 마세요.
//                            """,
//                                currentMonth,
//                                bsiDescription,
//                                cpiDescription,
//                                market,
//                                trend
//                        ))
//                ),
//                "functions", List.of(
//                        Map.of(
//                                "name", "generateMarketReport",
//                                "description", "주요 재료 가격 변동 및 카페 음료, 디저트 소비 동향을 요약한 시장 동향 월간 리포트를 생성합니다.\n" +
//                                        "                                    BSI와 CPI 데이터를 기반으로 카페 운영자가 고려해야 할 매출/지출 조정 방안, 마케팅 전략, 그리고 효율적인 자원 활용 방법에 대해 간략한 피드백도 포함됩니다.\n" +
//                                        "                                    모든 정보는 한국의 최신 뉴스와 공공 데이터를 기반으로 생성하세요..",
//                                "parameters", Map.of(
//                                        "type", "object",
//                                        "properties", Map.of(
//                                                "month", Map.of("type", "integer", "description", "2024년 해당 월"),
//                                                "BSI_description", Map.of("type", "string", "description", "오늘 기준 BSI 지수에 대한 설명"),
//                                                "CPI_description", Map.of("type", "string", "description", "오늘 기준 소비자물가지수 (CPI) 설명"),
//                                                "market_issue", Map.of("type", "string", "description", "주요 카페 재료의 가격 변동 (우유, 원두, 설탕 등)"),
//                                                "trend", Map.of("type", "string", "description", "한국 내 최신 카페 소비 동향"),
//                                                "recommendations", Map.of(
//                                                        "type", "array",
//                                                        "items", Map.of("type", "string"),
//                                                        "description", " 위 정보에 기반하여 카페 운영자를 위한 예측 및 권장 사항 목록:\n" +
//                                                                "                                                            1. BSI 하락 시 매출 감소 대응 전략\n" +
//                                                                "                                                            2. CPI 상승 시 원가 절감 방안\n" +
//                                                                "                                                            3. 소비 심리 활성화를 위한 마케팅 아이디어\n" +
//                                                                "                                                            \"\"\""
//                                                )
//                                        ),
//                                        "required", List.of("month", "BSI_description", "CPI_description", "market_issue", "trend", "recommendations")
//                                )
//                        )
//                ),
//                "function_call", Map.of("name", "generateMarketReport")
//        );
//
//        try {
//            String response = webClient.post()
//                    .uri("/chat/completions")
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            // JSON 응답 파싱
//            JsonNode rootNode = objectMapper.readTree(response);
//            JsonNode argumentsNode = rootNode.path("choices").get(0).path("message").path("function_call").path("arguments");
//
//            // argumentsNode가 문자열이므로 다시 JSON으로 파싱
//            String argumentsJson = argumentsNode.asText();
//            JsonNode parsedArguments = objectMapper.readTree(argumentsJson);
//
//            // JsonNode를 Map으로 변환
//            Map<String, Object> marketReport = objectMapper.convertValue(parsedArguments, Map.class);
//
//            return marketReport;
//
//        } catch (WebClientResponseException e) {
//            System.err.println("API 호출 오류: " + e.getResponseBodyAsString());
//            return Map.of("error", "API 호출 오류 발생");
//        } catch (Exception e) {
//            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
//            return Map.of("error", "예상치 못한 오류 발생");
//        }
//    }


    /////////////////////// 2. &#xB3D9;&#xC885; &#xC5C5;&#xACC4; &#xBE44;&#xAD50; &#xBD84;&#xC11D; &#xBCF4;&#xACE0;&#xC11C; &#xC0DD;&#xC131; (&#xC9C0;&#xC5ED; &#xAE30;&#xBC18;)
    ///
    public Map<String, Object> generateIndustryComparisonReport(Long memberId, YearMonth month) {

        Map<String, Object> monthlyIncome = posService.calculateAverageMonthlyMetrics(month);
        Map<String, Object> monthlyExpenseAverage = accountService.getMonthlyExpenseAverage(month);
        MonthlyIncomeDTO myIncome = posService.getMonthlyIncomeSummary(memberId, month);
        ExpenseDTO myExpense = accountService.showSimpleExpense(memberId, month);

        String content = String.format("""
                다음 데이터는 평균 카페 운영 관련 지출 및 매출 데이터입니다.
                - 평균 매출 정보: %s
                - 평균 지출 정보: %s
                
                다음 데이터는 나의 카페 운영 관련 지출 및 매출 데이터입니다.
                - 평균 나의 매출 정보: %s
                - 평균 나의 지출 정보: %s
                """, monthlyIncome, monthlyExpenseAverage, myIncome, myExpense
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "당신은 카페를 운영하는 사장님을 위한 시장 동향 보고서를 작성하는 AI입니다. " +
                                        "다음 데이터는 카페 운영 관련 지출 및 매출 데이터입니다: " + content +
                                        "이를 바탕으로 동종 업계와 비교하여 분석 결과, 권장 사항, 및 종합 추천을 JSON 형식으로 제공해주세요. " +
                                        "답변은 아나운서처럼 작성해주세요."
                        ),
                        Map.of("role", "user", "content",
                                """
                                다음 정보를 JSON 형식으로 정리하세요. 금액은 "약 ~ 만원" 형식으로 입력하며, 
                                입력된 금액이 소수점을 포함하거나 정수 형태인 경우 모두 1만 원 단위로 반올림하여 표현합니다.
                                   - 예: 5145000.00 → "약 515만원"
                                   - 예: 11814000 → "약 1181만원"
                                 :
                    
                                1. **average_sale**: "약 ~ 만원" 형식으로 된 주변 동종 업계 매출 평균을 입력합니다. (예: "약 1181만원")
                                2. **average_expense**: "약 ~ 만원" 형식으로 된 주변 동종 업계 지출 평균을 입력합니다. (예: "약 821만원")
                                3. **my_income**: "약 ~ 만원" 형식으로 된 나의 매출을 입력합니다. (예: "약 281만원")
                                4. **my_expense**: "약 ~ 만원" 형식으로 된 나의 지출을 입력합니다. (예: "약 181만원")
                                5. **sale_description**: 주변 업계와 비교한 매출 분석 결과를 서술합니다.
                                6. **expense_description**: 주변 업계와 비교한 지출 분석 결과를 서술합니다.
                                7. **recommendation**: 매출 및 지출 데이터를 종합적으로 고려한 운영 전략 및 효율화 방안에 대한 권장 사항을 제공합니다.
                                """
                        )
                ),
                "functions", List.of(
                        Map.of(
                                "name", "generateIndustryComparisonReport",
                                "description", "지출과 매출에 대한 비교 분석 리포트를 생성하며 종합 권장 사항을 제공합니다.",
                                "parameters", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "average_sale", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 매출 평균"),
                                                "average_expense", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 지출 평균"),
                                                "my_income", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 나의 매출"),
                                                "my_expense", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 나의 지출"),
                                                "sale_description", Map.of("type", "string", "description", "주변 카페와 비교한 매출 분석 결과"),
                                                "expense_description", Map.of("type", "string", "description", "주변 카페와 비교한 지출 분석 결과"),
                                                "recommendation", Map.of("type", "string", "description", "매출 및 지출 데이터를 종합적으로 고려한 권장 사항")
                                        ),
                                        "required", List.of("average_sale", "average_expense", "my_income", "my_expense", "sale_description", "expense_description", "recommendation")
                                )
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
            Map<String, Object> industryReport = objectMapper.convertValue(parsedArguments, Map.class);

            return industryReport;

        } catch (WebClientResponseException e) {
            System.err.println("API 호출 오류: " + e.getResponseBodyAsString());
            return Map.of("error", "API 호출 오류 발생");
        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            return Map.of("error", "예상치 못한 오류 발생");
        }
    }

//    public Map<String,Object> generateIndustryComparisonReport(Long memberId, YearMonth month) {
//
//            Map<String, Object> monthlyIncome = posService.calculateAverageMonthlyMetrics(month);
//            Map<String, Object> monthlyExpenseAverage = accountService.getMonthlyExpenseAverage(month);
//            MonthlyIncomeDTO myIncome  = posService.getMonthlyIncomeSummary(memberId, month);
//            ExpenseDTO myExpense = accountService.showSimpleExpense(memberId,month);
//
//
//        String content = String.format("""
//                    다음 데이터는 평균 카페 운영 관련 지출 및 매출 데이터입니다.
//                    - 평균 매출 정보: %s
//                    - 평균 지출 정보: %s
//
//                    다음 데이터는 나의 카페 운영 관련 지출 및 매출 데이터입니다.
//                    - 평균 나의 매출 정보: %s
//                    - 평균 나의 지출 정보: %s
//                    """, monthlyIncome, monthlyExpenseAverage, myIncome, myExpense
//            );
//        Map<String, Object> requestBody = Map.of(
//                "model", "gpt-4o",
//                "messages", List.of(
//                        Map.of("role", "system", "content",
//                                "당신은 카페를 운영하는 사장님을 위한 시장 동향 보고서를 작성하는 AI입니다. " +
//                                        "다음 데이터는 카페 운영 관련 지출 및 매출 데이터입니다: " + content +
//                                        "이를 바탕으로 동종 업계와 비교하여 분석 결과를 JSON 형식으로 제공해주세요. " +
//                                        "답변은 아나운서처럼 작성해주세요."
//                        ),
//                        Map.of("role", "user", "content",
//                                """
//                                다음 정보를 JSON 형식으로 정리하세요. 금액은 "약 ~ 만원" 형식으로 입력하며,
//                                입력된 금액이 소수점을 포함하거나 정수 형태인 경우 모두 1만 원 단위로 반올림하여 표현합니다.
//                                   - 예: 5145000.00 → "약 515만원"
//                                   - 예: 11814000 → "약 1181만원"
//                                 :
//
//                                1. **average_sale**: "약 ~ 만원" 형식으로 된 주변 동종 업계 매출 평균을 입력합니다. (예: "약 1181만원")
//                                2. **average_expense**: "약 ~ 만원" 형식으로 된 주변 동종 업계 지출 평균을 입력합니다. (예: "약 821만원")
//                                3. **my_income**: "약 ~ 만원" 형식으로 된 나의 매출을 입력합니다. (예: "약 281만원")
//                                4. **my_expense**: "약 ~ 만원" 형식으로 된 나의 지출을 입력합니다. (예: "약 181만원")
//                                5. **sale_description**: 주변 업계와 비교한 매출 분석 결과를 서술합니다. 분석 결과는 다음 요소를 포함해야 합니다:
//                                   - 주변 업계 대비 매출 비율 (예: "주변 카페들에 비해 10% 낮아요.")
//                                   - 매출이 주로 발생하는 시간대 (예: "아침 시간대에 매출이 높아요.")
//                                   - 카드와 현금 매출의 비율 차이 (예: "카드 거래가 대부분이에요.")
//                                   - 추천 사항: 매출 시간이 적은 시간의 매출 증대를 위한 전략 및 실행 가능한 조언을 포함합니다
//                                6. **expense_description**: 주변 업계와 비교한 지출 분석 결과를 서술합니다. 분석 결과는 다음 요소를 포함해야 합니다:
//                                   - 특정 지출 카테고리(예: 인건비, 공과금, 임대료 등)와 비교
//                                   - 해당 카테고리의 평균 지출 금액 (예: "임대료는 평균 100만원이에요.")
//                                   - 나의 지출이 평균 대비 몇 % 더 높은지 또는 낮은지 (예: "임대료가 15% 높/낮아요.")
//                                   - 추천 사항: 지출 중 평균 대비 높은 지출을 절감 및 효율화를 위한 실행 가능한 조언을 포함합니다
//                                """
//                        )
//                ),
//
//        "functions", List.of(
//                            Map.of(
//                                    "name", "generateIndustryComparisonReport",
//                                    "description", "지출과 매출에 대한 비교 분석 리포트를 생성합니다.",
//                                    "parameters", Map.of(
//                                            "type", "object",
//                                            "properties", Map.of(
//                                                "average_sale", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 매출 평균"),
//                                                "average_expense", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 지출 평균"),
//                                                "my_income", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 나의 매출"),
//                                                "my_expense", Map.of("type", "string", "description", "약 ~ 만원 형식으로 된 나의 지출"),
//                                                "sale_description", Map.of("type", "string", "description", "주변 카페와 비교한 카테고리 별 지출 분석 결과"),
//                                                "expense_description", Map.of("type", "string", "description", "주변 카페와 비교한 시간과 매출 타입(카드/현금) 분석 결과")
//                                            )
//                                    ),
//                                    "required", List.of("average_sale", "average_expense","my_income", "my_expense", "sale_description","expense_description")
//                            )
//                    ),
//                    "function_call", Map.of("name", "generateIndustryComparisonReport")
//
//            );
//
//
//        try {
//            String response = webClient.post()
//                    .uri("/chat/completions")
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            // JSON 응답 파싱
//            JsonNode rootNode = objectMapper.readTree(response);
//            JsonNode argumentsNode = rootNode.path("choices").get(0).path("message").path("function_call").path("arguments");
//
//            // argumentsNode가 문자열이므로 다시 JSON으로 파싱
//            String argumentsJson = argumentsNode.asText();
//            JsonNode parsedArguments = objectMapper.readTree(argumentsJson);
//
//            // JsonNode를 Map으로 변환
//            Map<String, Object> marketReport = objectMapper.convertValue(parsedArguments, Map.class);
//
//            return marketReport;
//
//        } catch (WebClientResponseException e) {
//            System.err.println("API 호출 오류: " + e.getResponseBodyAsString());
//            return Map.of("error", "API 호출 오류 발생");
//        } catch (Exception e) {
//            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
//            return Map.of("error", "예상치 못한 오류 발생");
//        }
//    }

    @Transactional
    public boolean previousMonthReportChecker(Long memberId) {
        // 현재 날짜 기준 전 달 계산
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);
        LocalDate previousReportMonth = previousMonth.atDay(1); // 전 달의 첫 번째 날

        // BusinessRegistration 조회
        BusinessRegistration businessRegistration = businessService.getBusinessIdByMemberID(memberId);


        if (businessRegistration == null) {
            throw new IllegalArgumentException("회원 ID: " + memberId + "에 해당하는 사업자 등록 정보가 존재하지 않습니다.");
        }

        // 리포트 타입 리스트 (MARKET_REPORT와 INDUSTRY_REPORT)
        List<String> reportTypes = List.of("MARKET_REPORT", "INDUSTRY_REPORT");

        // 각 리포트 타입에 대해 존재 여부 확인
        boolean allReportsExist = reportTypes.stream().allMatch(reportType ->
                queryFactory.selectFrom(QReport.report)
                        .where(
                                QReport.report.businessRegistration.businessRegistrationId.eq(businessRegistration.getBusinessRegistrationId()),
                                QReport.report.reportMonth.eq(previousReportMonth),
                                QReport.report.reportType.eq(reportType)
                        )
                        .fetchOne() != null
        );

        return allReportsExist;
    }



}
