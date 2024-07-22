package com.banking.banking_app.service;

import com.banking.banking_app.dto.AccountDto;
import com.banking.banking_app.dto.DepositDto;
import com.banking.banking_app.dto.WithdrawDto;

import java.util.List;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);

    AccountDto updateAccount(Long id, AccountDto accountDto);

    List<AccountDto> getAccountsByUserId(Long userId);

    AccountDto deposit(DepositDto depositDto);
    AccountDto withdraw(WithdrawDto withdrawDto);

}
