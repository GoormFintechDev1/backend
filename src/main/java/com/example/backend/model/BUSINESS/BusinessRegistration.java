package com.example.backend.model.BUSINESS;
import com.example.backend.model.BANK.Account;
import com.example.backend.model.BaseTime;
import com.example.backend.model.POS.Pos;
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
    private Long businessRegistrationId;

    // 업태
    @Column(name = "business_type", nullable = false)
    private String businessType ;

    // 종목
    @Column(name = "business_item", nullable = false)
    private String businessItem;

    // 사업자등록번호 -> String으로 받기 v
    @Column(name = "br_num", nullable = false, unique = true)
    private String brNum;

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

    // 포스 식별 번호
    @OneToOne
    @JoinColumn(name = "pos_id", nullable = true)
    private Pos pos;

    // 계좌 식별 번호
    @OneToOne
    @JoinColumn(name = "account_id", nullable = true)
    public Account account;


}
