package com.banking.banking_app.mapper;

import com.banking.banking_app.dto.AccountDto;
import com.banking.banking_app.entity.Account;
import com.banking.banking_app.entity.User;

public class AccountMapper {

    public static AccountDto mapToAccountDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .accountHolderName(account.getAccountHolderName())
                .balance(account.getBalance())
                .userId(account.getUser().getId())
                .build();
    }

    public static Account mapToAccount(AccountDto accountDto) {
        Account account = Account.builder()
                .id(accountDto.getId())
                .accountHolderName(accountDto.getAccountHolderName())
                .balance(accountDto.getBalance())
                .build();
        User user = new User();
        user.setId(accountDto.getUserId());
        account.setUser(user);
        return account;
    }

    public static Account mapToAccount(AccountDto accountDto, User user) {
        return Account.builder()
                .id(accountDto.getId())
                .accountHolderName(accountDto.getAccountHolderName())
                .balance(accountDto.getBalance())
                .user(user)
                .build();
    }
}
