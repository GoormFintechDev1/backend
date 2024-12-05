package com.example.backend.repository;

import com.example.backend.model.BUSINESS.BusinessRegistration;
import com.example.backend.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRegistrationRepository extends JpaRepository<BusinessRegistration, Long> {

}
