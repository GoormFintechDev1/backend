package com.example.backend.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExpenseWeekDTO {
    // 1주차
    private BigDecimal week1;
    // 2주차
    private BigDecimal week2;
    // 3주차
    private BigDecimal week3;
    // 4주차
    private BigDecimal week4;
    // 5주차
    private BigDecimal week5;
}
