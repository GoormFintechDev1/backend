package com.example.backend.repository;

import com.example.backend.model.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {
}
