package com.example.backend.service;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportService {

    @Value("${OPENAI_API_KEY}")
    private String openAiApiKey;
    private final JPAQueryFactory queryFactory;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1";
    private WebClient webClient;

    // 생성자를 통해 WebClient를 초기화
    @Autowired
    public ReportService(@Value("${OPENAI_API_KEY}") String openAiApiKey, JPAQueryFactory queryFactory) {
        this.openAiApiKey = openAiApiKey;
        this.queryFactory = queryFactory;

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
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 카페를 운영하는 사장님을 위한 시장 동향 보고서를 작성하는 AI입니다."),
                        Map.of("role", "user", "content", "커피 원두, 우유, 설탕 등의 주요 재료 가격 변동과 외식 소비 동향을 JSON 형식으로 정리해주세요.")
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
    public String generateIndustryComparisonReport(Long memberId) {
        try {
            // 사업장 정보 가져오기
            Map<String, Object> businessRegistration = fetchBusinessRegistration(memberId);
            if (businessRegistration == null) {
                return "사업장 정보 없음";
            }
            String region = (String) businessRegistration.get("region");

            List<Map<String, Object>> accountHistory = fetchAccountHistoryByRegion(region);
            List<Map<String, Object>> posSales = fetchPosSalesByRegion(region);

            String content = String.format("""
                다음 데이터는 카페 운영 관련 지출 및 매출 데이터입니다.
                - accountHistory: %s
                - posSales: %s
                """, accountHistory, posSales);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", "당신은 카페를 운영하는 사장님을 위한 시장 동향 보고서를 작성하는 AI입니다. 동종 업계와 비교한 분석을 JSON 형식으로 작성하세요."),
                    Map.of("role", "user", "content", content)
            ));

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


    // 사용자의 사업장 정보 가져오기
    private Map<String, Object> fetchBusinessRegistration(Long memberId) {
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;
        BusinessRegistration registration = queryFactory.selectFrom(qBusinessRegistration)
                .where(qBusinessRegistration.member.id.eq(memberId))
                .fetchOne();

        if (registration == null) return null;

        String address = registration.getAddress();
        String region = extractRegionFromAddress(address);

        return Map.of(
                "address", address,
                "region", region,
                "companyName", registration.getCompanyName()
        );
    }

    // 특정 지역의 지출 내역 평균 내기
    private List<Map<String, Object>> fetchAccountHistoryByRegion(String region) {
        QAccountHistory qAccountHistory = QAccountHistory.accountHistory;
        QAccount qAccount = QAccount.account;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;

        return queryFactory.selectFrom(qAccountHistory)
                .join(qAccountHistory.accountId, qAccount)
                .join(qAccount.business, qBusinessRegistration)
                .where(qBusinessRegistration.address.contains(region))
                .fetch()
                .stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", record.getTransactionDate() != null ? record.getTransactionDate().toString() : "");
                    map.put("amount", record.getAmount());
                    map.put("category", record.getCategory());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // 특정 지역의 POS 매출 내역 평균 내기
    private List<Map<String, Object>> fetchPosSalesByRegion(String region) {
        QPosSales qPosSales = QPosSales.posSales;
        QPos qPos = QPos.pos;
        QBusinessRegistration qBusinessRegistration = QBusinessRegistration.businessRegistration;

        return queryFactory.selectFrom(qPosSales)
                .join(qPosSales.pos, qPos)
                .join(qPos.businessRegistration, qBusinessRegistration)
                .where(qBusinessRegistration.address.contains(region))
                .fetch()
                .stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", record.getSaleTime() != null ? record.getSaleTime().toString() : "");
                    map.put("amount", record.getTotalAmount());
                    map.put("paymentType", record.getPaymentType());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // 주소에서 구(region) 추출
    private String extractRegionFromAddress(String address) {
        if (address == null || address.isEmpty()) return "";
        String[] parts = address.split(" ");
        return parts.length > 1 ? parts[1] : "";
    }
}
