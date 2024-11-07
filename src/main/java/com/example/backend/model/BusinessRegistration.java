package com.example.backend.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "business_registration")
public class BusinessRegistration extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_registration_id")
    private Long id;

    // 회원 식별 번호
    @OneToOne
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;

    // 사업 카테고리 식별번호
    @ManyToOne
    @JoinColumn(name = "business_category_id", nullable = false)
    private BusinessCategory businessCategory;

    // 사업자등록번호
    @Column(name = "br_num", nullable = false, unique = true)
    private Long brNum;

    // 사업장 주소
    @Column(name = "address", nullable = true, length = 255)
    private String address;

    // 사업 시작일
    @Column(name = "business_start_date")
    private LocalDate businessStartDate;

    // 대표자명
    @Column(name = "representative_name")
    private String representativeName;

    // 상호
    @Column(name = "company_name")
    private String companyName;

}
