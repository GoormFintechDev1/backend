package com.example.backend.repository;

import com.example.backend.model.Pos;
import com.example.backend.model.PosSales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosSalesRepository extends JpaRepository<PosSales, Long> {
}
