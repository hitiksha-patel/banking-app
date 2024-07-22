package com.banking.banking_app.service.impl;

import com.banking.banking_app.dto.AccountDto;
import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.entity.Account;
import com.banking.banking_app.entity.User;
import com.banking.banking_app.exception.UserNotFoundException;
import com.banking.banking_app.mapper.AccountMapper;
import com.banking.banking_app.mapper.UserMapper;
import com.banking.banking_app.repository.AccountRepository;
import com.banking.banking_app.repository.UserRepository;
import com.banking.banking_app.service.AccountService;
import com.banking.banking_app.util.AuthUtil;
import com.banking.banking_app.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository){
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AccountDto createAccount(AccountDto accountDto) {
        try {
            User user = userRepository.findById(accountDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + accountDto.getUserId()));
            Account account = AccountMapper.mapToAccount(accountDto, user);
            if (account.getBalance() == null) {
                account.setBalance(0.0);
            }
            Account savedAccount = accountRepository.save(account);
            return AccountMapper.mapToAccountDto(savedAccount);
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            throw e; // Re-throw the exception to be handled by the controller
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while creating the account", e); // Re-throw as a generic runtime exception
        }
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Long accountId, AccountDto accountDto) {
        try {
            User authenticatedUser = authUtil.getAuthenticatedUser();

            Account existingAccount = accountRepository.findById(accountId)
                    .orElseThrow(() -> new UserNotFoundException("Account not found with id " + accountId));

            if (!existingAccount.getUser().getId().equals(authenticatedUser.getId())) {
                throw new RuntimeException("You do not have permission to update this account.");
            }

            existingAccount.setAccountHolderName(accountDto.getAccountHolderName());

            Account updatedAccount = accountRepository.save(existingAccount);
            return AccountMapper.mapToAccountDto(updatedAccount);

        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            throw new UserNotFoundException("User not found");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong!");
        }
    }

    @Override
    public List<AccountDto> getAccountsByUserId(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(AccountMapper::mapToAccountDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto getAccountById(Long id) {
        try {
            User authenticatedUser = authUtil.getAuthenticatedUser();
            Account account = accountRepository
                    .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));

            if (!account.getUser().getId().equals(authenticatedUser.getId())) {
                throw new RuntimeException("You do not have permission to update this account.");
            }
            return AccountMapper.mapToAccountDto(account);
        } catch (RuntimeException e){
            throw new RuntimeException("Something went wrong!");
        }
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));
        Double total = account.getBalance() + (Double.valueOf(amount));
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));

        if(account.getBalance().compareTo(Double.valueOf(amount)) < 0){
            throw new RuntimeException("Insufficient amount");
        }
        Double total = account.getBalance() - (Double.valueOf(amount));
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
