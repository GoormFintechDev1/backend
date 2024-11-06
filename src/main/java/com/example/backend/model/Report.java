package com.example.backend.model;

import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "report")
public class Report extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    // 사업자 식별 번호
    @ManyToOne
    @JoinColumn(name = "business_registration_id", nullable = false)
    private BusinessRegistration businessRegistrationId;

    // 레포트 대상 월
    @Column(name = "report_month")
    private LocalDate reportMonth;

    // 해당 월의 매출 총액
    @Column(name = "revenue")
    private Long revenue;

    // 해당 월의 지출 총액
    @Column(name = "expenses")
    private Long expenses;

    // 해당 월의 순수익
    @Column(name = "profit")
    private Long profit;

    // 평가
    @Column(name = "stability_rating", length = 20)
    private String stabilityRating;

    //업계 평균 매출
    @Column(name = "industry_avg_revenue")
    private Long industryAvgRevenue;

    //업계 평균 지출
    @Column(name = "industry_avg_expenses")
    private Long industryAvgExpenses;

    // 업계 평균 수익
    @Column(name = "industry_avg_profit")
    private Long industryAvgProfit;

    // BSI 지수
    @Column(name = "bsi_index")
    private Float bsiIndex;
}