package com.example.backend.repository;


import com.example.backend.model.POS.Pos;
import com.example.backend.model.POS.PosSales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PosSalesRepository extends JpaRepository<PosSales, Long> {
    boolean existsByOrderTimeAndPosId(LocalDateTime orderTime, Pos posId);

    boolean existsByOrderTimeAndProductName(LocalDateTime orderDate, String productName);
}
