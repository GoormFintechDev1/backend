package com.example.backend.repository;

import com.example.backend.model.Pos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosRepository extends JpaRepository<Pos, Long> {
}
