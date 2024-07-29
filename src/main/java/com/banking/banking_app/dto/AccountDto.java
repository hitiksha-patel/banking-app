package com.banking.banking_app.dto;

import com.banking.banking_app.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;
    private String accountHolderName;
    private Double balance;
    private Long userId;
    private AccountType accountType;
}
