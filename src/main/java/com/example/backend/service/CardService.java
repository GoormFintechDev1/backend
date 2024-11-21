package com.example.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.backend.dto.account.CardDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CardService {
	@Autowired
	private AccountService accountService;
	@Value("${card.json.file.path:card_data.json}")
	private String jsonFilePath;
	
	public List<CardDTO> loadCardsFromJson(String filePath) {
		ObjectMapper objectMapper = new ObjectMapper();
		
		log.info(filePath + " :: filePath");
		
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
			if (inputStream == null) {
				throw new RuntimeException("File not found: " + filePath);
			}
			// JSON 파일을 읽어서 Card 리스트로 변환
			return objectMapper.readValue(inputStream, new TypeReference<List<CardDTO>>() {});
		} catch (IOException e) {
			throw new RuntimeException("Failed to load cards from JSON file: " + filePath, e);
		}
	}
	
	public List<Map<String, Object>> recommendCards(YearMonth month, Long memberId) {
		List<CardDTO> cards = loadCardsFromJson(jsonFilePath);
		log.info(cards);
		
		List<Map<String, Object>> recommendations = new ArrayList<>();
		Map<String, BigDecimal> spending = accountService.calculateCategoryWiseExpenses(month, memberId);
		
		log.info(spending + " :: spending");
		
		for (CardDTO card :cards) {
			BigDecimal totalSavings = BigDecimal.ZERO;
			for (String benefit : card.getBenefits()) {
				for (Map.Entry<String, BigDecimal> entry : spending.entrySet()) {
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
                            totalSavings = totalSavings.add(earning); // 적립도 절약으로 간주
						}
					}
				}
			}
			
			Map<String, Object> result = new HashMap<>();
			result.put("Card Name", card.getCardName());
			result.put("Corporate Name", card.getCorporateName());
			result.put("Total Saving", totalSavings);
			result.put("Image URL", card.getImageUrl());
			recommendations.add(result);
		}
		
		recommendations.sort((a, b) -> ((BigDecimal) b.get("Total Saving")).compareTo((BigDecimal) a.get("Total Saving")));
		return recommendations;
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

