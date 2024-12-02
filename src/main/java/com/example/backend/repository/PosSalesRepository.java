package com.example.backend.repository;


import com.example.backend.model.POS.PosSales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosSalesRepository extends JpaRepository<PosSales, Long> {
}
