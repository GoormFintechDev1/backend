package com.example.backend.repository;

import com.example.backend.model.Goals;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalsRepository extends JpaRepository<Goals, Long> {
}
