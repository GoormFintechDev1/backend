package com.example.backend.model.BUSINESS;

import com.example.backend.model.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "goals")
public class Goals extends BaseTime {

    // 식별 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    // 사업자 ID
    @ManyToOne
    @JoinColumn(name = "business_id")
    private BusinessRegistration businessId;

    // 목표월
    @Column(name = "goal_month")
    private YearMonth goalMonth;

    // 매출 목표
    @Column(name = "revenue_goal", precision = 15, scale = 0)
    private BigDecimal revenueGoal;

    // 지출 목표
    @Column(name = "expense_goal", precision = 15, scale = 0)
    private BigDecimal expenseGoal;

}
