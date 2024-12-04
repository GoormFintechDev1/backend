package com.example.backend.repository;
import com.example.backend.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // Optional을 사용하여 member를 찾지 못했을 때 null 대신 Optional.empty() 반환
    // Null Pointer Exception을 방지할 수 있음

    // 중복 여부 검사
    Optional<Member> findByLoginId(String LoginId);
    Optional<Member> findByPhoneNumber(String phoneNumber);

    Optional<Member> findById(Long memberId);

    Optional<Member> findByEmail(String email);
}
