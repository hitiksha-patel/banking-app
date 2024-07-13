package com.banking.banking_app.mapper;

import com.banking.banking_app.dto.AccountDto;
import com.banking.banking_app.entity.Account;
import com.banking.banking_app.entity.User;

public class AccountMapper {
    public static Account mapToAccount(AccountDto accountDto, User user) {
        if (accountDto == null || user == null) {
            return null;
        }
        return Account.builder()
                .id(accountDto.getId())
                .accountHolderName(accountDto.getAccountHolderName())
                .balance(accountDto.getBalance())
                .user(user)
                .build();
    }

    public static AccountDto mapToAccountDto(Account account) {
        if (account == null) {
            return null;
        }
        return AccountDto.builder()
                .id(account.getId())
                .accountHolderName(account.getAccountHolderName())
                .balance(account.getBalance())
                .userId(account.getUser().getId())
                .build();
    }

}

//
//        Account account = new Account(
//                accountDto.getId(),
//                accountDto.getAccountHolderName(),
//                accountDto.getBalance()
//        );
//        return account;
//    }
//    public static AccountDto mapToAccountDto(Account account){
//        AccountDto accountDto = new AccountDto(
//                account.getId(),
//                account.getAccountHolderName(),
//                account.getBalance()
//        );
//        return accountDto;
//    }
//}
