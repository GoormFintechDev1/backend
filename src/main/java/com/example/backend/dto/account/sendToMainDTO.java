package com.example.backend.dto.account;


import com.example.backend.model.BANK.Account;
import com.example.backend.model.BANK.AccountHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class sendToMainDTO {
    private Account account; // 계좌 정보
    private List<AccountHistory> accountHistory; // 거래 내역 리스트
}
