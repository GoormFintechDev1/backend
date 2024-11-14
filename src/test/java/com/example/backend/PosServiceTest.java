package com.example.backend;

import com.example.backend.service.PosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PosServiceTest {

    @Autowired
    private PosService posService;


    @Test
    public void testCalculateAverageMonthlyMetrics() {
        YearMonth testMonth = YearMonth.of(2024, 10);
        Map<String, Object> averageMetrics = posService.calculateAverageMonthlyMetrics(testMonth);

        System.out.println("Average Monthly Income: " + averageMetrics.get("averageMonthlyIncome"));
        System.out.println("Average Monthly Card Income: " + averageMetrics.get("averageMonthlyCardIncome"));
        System.out.println("Average Monthly Cash Income: " + averageMetrics.get("averageMonthlyCashIncome"));
        System.out.println("Morning Sales: " + averageMetrics.get("morningSales"));
        System.out.println("Afternoon Sales: " + averageMetrics.get("afternoonSales"));
        System.out.println("Evening Sales: " + averageMetrics.get("eveningSales"));

        // 최고 매출 시간대
        String peakSalesPeriod = (String) averageMetrics.get("peakSalesPeriod");
        System.out.println("Peak Sales Period: " + peakSalesPeriod);

        assertNotNull(peakSalesPeriod, "Peak Sales Period should not be null");
    }
}
