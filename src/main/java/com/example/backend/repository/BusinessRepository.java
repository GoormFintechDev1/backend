package com.example.backend.repository;

import com.example.backend.model.BusinessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<BusinessRegistration, Long> {

    // 사업자 등록 번호가 존재하는 지 확인
    Optional<BusinessRegistration> findByBrNum(String BrNum);

    Optional<BusinessRegistration> findByRepresentativeName(String representativeName);

}
