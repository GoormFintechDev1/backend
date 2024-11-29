package com.example.backend.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.backend.dto.card.CardDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

//////////// 카드 추천 로직
/// 1. 유저의 소비 카테고리 중 상위 3개를 추출
/// 2. 상위 3개에 해당하는 카드 혜택의 합이 가장 높은 카드를 추천
/// 3. 추천 카드가 5개 이상일 경우, 카드 랭킹이 높은 순으로 정렬


@Service
@Log4j2
public class CardService {
	@Autowired
	private AccountService accountService;
	@Value("${card.py.data.path}")
	private String jsonFilePath;
	
	public List<CardDTO> loadCardsFromJson(String filePath) {
		ObjectMapper objectMapper = new ObjectMapper();
		
		log.info(filePath + " :: filePath");
		
		try {
			// 파일의 경로를 resources 로 설정했을 때,
//			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
//			if (inputStream == null) {
//				throw new RuntimeException("File not found: " + filePath);
//			}
			// 파일의 경로를 root 로 설정했을 때,
			File jsonFile = new File(filePath);
			// JSON 파일을 읽어서 Card 리스트로 변환
			return objectMapper.readValue(jsonFile, new TypeReference<List<CardDTO>>() {});
		} catch (IOException e) {
			throw new RuntimeException("Failed to load cards from JSON file: " + filePath, e);
		}
	}

	public List<Map<String, Object>> recommendCards(YearMonth month, Long memberId) {
		// JSON 파일 불러오기
		List<CardDTO> cards = loadCardsFromJson(jsonFilePath);

		// 소비 내역에서 상위 3개 카테고리 추출
		Map<String, BigDecimal> spending = accountService.calculateCategoryWiseExpenses(month, memberId);
		Map<String, BigDecimal> top3Spending = spending.entrySet().stream()
				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
				.limit(3)
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
				));
		log.info("Top 3 Spending: " + top3Spending);

		// 카드 추천 로직
		List<Map<String, Object>> recommendations = new ArrayList<>();

		for (CardDTO card : cards) {
			//////// 절약한 총 금액 계산
			BigDecimal totalSavings = BigDecimal.ZERO;
			for (String benefit : card.getBenefits()) {
				for (Map.Entry<String, BigDecimal> entry : top3Spending.entrySet()) {
					String category = entry.getKey();
					BigDecimal amount = entry.getValue();

					if (benefit.contains(category)) {
						if (benefit.contains("할인")) {
							int discountRate = extractPercentage(benefit);
							BigDecimal discount = amount.multiply(BigDecimal.valueOf(discountRate)).divide(BigDecimal.valueOf(100));
							totalSavings = totalSavings.add(discount);
						} else if (benefit.contains("적립")) {
							int earningRate = extractPercentage(benefit);
							BigDecimal earning = amount.multiply(BigDecimal.valueOf(earningRate)).divide(BigDecimal.valueOf(100));
							totalSavings = totalSavings.add(earning);
						}
					}
				}
			}

			// 카드 랭킹
			int ranking = Integer.parseInt(card.getRanking());

			Map<String, Object> result = new HashMap<>();
			result.put("Card Name", card.getCardName());
			result.put("Corporate Name", card.getCorporateName());
			result.put("Total Saving", totalSavings);
			result.put("Ranking", ranking);
			result.put("Image URL", card.getImageUrl());
			recommendations.add(result);
		}

		// 정렬: 총 절약 금액 내림차순 -> 랭킹 오름차순
		recommendations.sort((a, b) -> {
			int compareSavings = ((BigDecimal) b.get("Total Saving")).compareTo((BigDecimal) a.get("Total Saving"));
			if (compareSavings != 0) return compareSavings;
			return Integer.compare((int) a.get("Ranking"), (int) b.get("Ranking"));
		});

		// 상위 5개 카드 반환
		return recommendations.stream().limit(5).collect(Collectors.toList());
	}


	private int extractPercentage(String text) {
		try {
			String[] parts = text.split("%");
			String number = parts[0].replaceAll("[^0-9]", "");
			return Integer.parseInt(number);
		} catch (Exception e) {
			return 0;
		}
	}

}
