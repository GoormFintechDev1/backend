package com.example.backend.repository;


import com.example.backend.model.BANK.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {


}
