package com.example.backend.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.backend.service.BANK.AccountService;
import com.example.backend.dto.account.ExpenseDetailDTO;
import com.example.backend.dto.account.ExpenseDetailDTO.ExpenseDetail;
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
			List<CardDTO> cardList = objectMapper.readValue(jsonFile, new TypeReference<List<CardDTO>>() {});
			
			String[] keywords = {"공과금", "쇼핑", "주유", "마트"};
	        List<CardDTO> filteredCards = cardList.stream()
	            .map(card -> {
	                // benefits 필터링
	                List<String> filteredBenefits = card.getBenefits().stream()
	                        .filter(benefit -> Arrays.stream(keywords).anyMatch(benefit::contains))
	                        .collect(Collectors.toList());
	                card.setBenefits(filteredBenefits); // 필터링된 benefits 설정
	                return card;
	            })
	            .filter(card -> !card.getBenefits().isEmpty()) // 필터링 후 benefits가 비어 있지 않은 카드만 유지
	            .collect(Collectors.toList());
			return filteredCards;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load cards from JSON file: " + filePath, e);
		}
	}

	public List<Map<String, Object>> recommendCards(YearMonth month, Long memberId) {
		// JSON 파일 불러오기
		List<CardDTO> cards = loadCardsFromJson(jsonFilePath);

		// 카드 DTO 에서 지출 상세 정보를 가져온다 
		List<ExpenseDetailDTO.ExpenseDetail> expenseDetails = accountService.getExpenseDetails(month, memberId);
		
		// 카드 추천 로직
		List<Map<String, Object>> recommendations = new ArrayList<>();

		// 카테고리 매핑 키워드
		Map<String, List<String>> categoryKeywords = new HashMap<>();
		categoryKeywords.put("재료비", Arrays.asList("재료비", "쇼핑", "재료", "마트"));
		
		for (CardDTO card : cards) {
			//////// 절약한 총 금액 계산
			BigDecimal totalSavings = BigDecimal.ZERO;
			
			// 할인을 포함한 지출 상세 정보 저장
			List<Map<String, Object>> savingDetails = new ArrayList<>();
			
		    log.info("=============================");
		    log.info("Card:: " + card.getCardName());
		    // 지출 세부 내역 조회
		    for (ExpenseDetail expense : expenseDetails) {
		    	String category = expense.getCategory();
		    	// 지출 내역의 카테고리명이 매핑 키워드와 일치하는지 확인하여 keywords 로 저장
		    	List<String> keywords = categoryKeywords.getOrDefault(category, Arrays.asList(category));

		    	// 카테고리와 일치하는 혜택 정보 필터링
		    	List<String> matchBenefits = card.getBenefits().stream()	// 카드 정보에서 혜택을 순회하여 
		    			.filter(benefit -> keywords.stream().anyMatch(benefit::contains))	// 키워드와 일치 여부를 확인
		    			.collect(Collectors.toList());	// 조건에 맞는 혜택을 리스트화
		    	
		    	// 일치하는 혜택 정보가 없으면 스킵
		    	if (matchBenefits.isEmpty()) continue;
		    	
		    	Optional<BigDecimal> maxDiscountRate = matchBenefits.stream()
		    			.filter(benefit -> isMatchingBenefit(benefit, expense, keywords))
		    			.map(this::extractDiscountRate)
		    			.max(Comparator.naturalOrder());
		    	
		    	// 할인율이 존재하는 경우 할인율 및 절약 금액 계산
		    	if (maxDiscountRate.isPresent()) {
		    		BigDecimal discountRate = maxDiscountRate.get();
		    		BigDecimal savings = expense.getAmount()
		    				.multiply(discountRate)
		    				.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
		    		
		    		totalSavings = totalSavings.add(savings);
		    		
		    		// 지출 항목별 절약 내역 추가
		    		log.info("Category:: " + category);
		    		log.info("keywords:: " + keywords);
		    		log.info("Note:: " + expense.getNote());
		    		log.info("Matched Benefit:: " + matchBenefits);
		    		log.info("Discount:: " + discountRate + "%");
		    		log.info("Savings:: " + savings);
		    		
		            Map<String, Object> savingDetail = new HashMap<>();
		            savingDetail.put("category", category);
		            savingDetail.put("storeName", expense.getStoreName());
		            savingDetail.put("note", expense.getNote());
		            savingDetail.put("benefit", matchBenefits);
		            savingDetail.put("amount", expense.getAmount());
		            savingDetail.put("discountRate", discountRate + "%");
		            savingDetail.put("saving", savings);
		            
		            savingDetails.add(savingDetail);
		    	}
		    }
		    
		    log.info("=============================");
		
		// 카테고리별 지출 합계를 계산
//      Map<String, Map<String, BigDecimal>> results = calculateExpenseSums(expenseDetails);
		
//      경우에 따라 지출 세부 내역 확인이 필요해서 상세 조회 로직으로 교체  
//		Map<String, BigDecimal> spending = accountService.calculateCategoryWiseExpenses(month, memberId);
		
//		소비 내역에서 상위 3개 카테고리 추출 > 현재 상위 3개 카테고리를 뽑을 정도로 카테고리가 많지 않아서 보류
//		Map<String, BigDecimal> top3Spending = spending.entrySet().stream()
//				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
//				.limit(3)
//				.collect(Collectors.toMap(
//						Map.Entry::getKey,
//						Map.Entry::getValue,
//						(oldValue, newValue) -> oldValue,
//						LinkedHashMap::new
//				));
//		log.info("Top 3 Spending: " + top3Spending);

			// 카드 랭킹
			int ranking = Integer.parseInt(card.getRanking());

			Map<String, Object> result = new HashMap<>();
			result.put("cardName", card.getCardName());
			result.put("corporateName", card.getCorporateName());
			result.put("totalSaving", totalSavings);
			result.put("ranking", ranking);
			result.put("imageURL", card.getImageURL());
			result.put("benefits", card.getBenefits());
			result.put("savingDetails", savingDetails);
			recommendations.add(result);
		}

		// 정렬: 총 절약 금액 내림차순 -> 랭킹 오름차순
		recommendations.sort((a, b) -> {
			int compareSavings = ((BigDecimal) b.get("totalSaving")).compareTo((BigDecimal) a.get("totalSaving"));
			if (compareSavings != 0) return compareSavings;
			return Integer.compare((int) a.get("ranking"), (int) b.get("ranking"));
		});

		// 상위 5개 카드 반환
		return recommendations.stream().limit(5).collect(Collectors.toList());
	}


	private BigDecimal extractDiscountRate(String benefit) {
		Pattern pattern = Pattern.compile("(\\d+)%");
		Matcher matcher = pattern.matcher(benefit);
		if (matcher.find()) {
			return new BigDecimal(matcher.group(1));
		}
		return BigDecimal.ZERO;
	}
	
	private boolean isMatchingBenefit(String benefit, ExpenseDetail expense, List<String> keywords) {
		// 혜택 중 온라인이라는 키워드를 가지는 경우 true
    	boolean isOnline = benefit.contains("온라인");
    	
		if (isOnline) {	// 혜택에 온라인 정보가 있으면, 지출 카테고리가 재료비이고 온라인 결제 정보를 조회
			return expense.getCategory().contains("재료비") && expense.getNote().contains("온라인");
		} else {	// 혜택에 온라인 정보가 없는 경우, 결제(Note)가 온라인이면 false 외에는 모두 true
			if( expense.getNote().contains("온라인")) {
		 	 			return false;
			}
			
			return true;
		}
	}
}
