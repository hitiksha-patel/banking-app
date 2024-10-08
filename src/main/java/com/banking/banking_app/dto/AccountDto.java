package com.banking.banking_app.dto;

import com.banking.banking_app.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String accountHolderName;
    private Double balance;
    private Long userId;
    private AccountType accountType;
    private String accountNumber;
}
