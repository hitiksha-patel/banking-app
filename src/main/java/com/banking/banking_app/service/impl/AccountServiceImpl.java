package com.banking.banking_app.service.impl;

import com.banking.banking_app.dto.AccountDto;
import com.banking.banking_app.entity.Account;
import com.banking.banking_app.entity.User;
import com.banking.banking_app.mapper.AccountMapper;
import com.banking.banking_app.repository.AccountRepository;
import com.banking.banking_app.repository.UserRepository;
import com.banking.banking_app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository){
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        User user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Account account = AccountMapper.mapToAccount(accountDto, user);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {

        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));
        BigDecimal total = account.getBalance().add(BigDecimal.valueOf(amount));
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));

        if(account.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0){
            throw new RuntimeException("Insufficient amount");
        }
        BigDecimal total = account.getBalance().subtract(BigDecimal.valueOf(amount));
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {

        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map(AccountMapper::mapToAccountDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));

        accountRepository.deleteById(id);
    }


}
