package com.example.backend.repository;


import com.example.backend.model.POS.Pos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosRepository extends JpaRepository<Pos, Long> {
}
