package com.example.backend.dto.pos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

@Data
@AllArgsConstructor
public class DailyIncomeDTO {

    private LocalDate date; // 일자
    private BigDecimal income;
}
