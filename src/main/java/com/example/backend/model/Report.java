package com.example.backend.model;

import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    // 레포트 유형 (MARKET_REPORT, INDUSTRY_COMPARISON)
    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType;

    // 보고서 내용 (JSON 형태)
    @Lob
    @Column(name="content", nullable = false, columnDefinition = "JSON")
    private String content;

    // 레포트 생성일
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}