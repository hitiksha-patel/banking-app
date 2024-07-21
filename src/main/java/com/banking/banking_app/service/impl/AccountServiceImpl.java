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
    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository){
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AccountDto createAccount(AccountDto accountDto) {
        try {
            System.out.println("Starting account creation for user ID: " + accountDto.getUserId());

            // Find the user by ID
            User user = userRepository.findById(accountDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + accountDto.getUserId()));

            System.out.println("User found: " + user);

            // Map the account DTO to an account entity
            Account account = AccountMapper.mapToAccount(accountDto, user);

            System.out.println("Account mapped: " + account);

            // Ensure balance is set before saving
            if (account.getBalance() == null) {
                account.setBalance(0.0);
            }

            // Log the account details before saving
            System.out.println("Saving account with balance: " + account.getBalance());

            // Save the account entity
            Account savedAccount = accountRepository.save(account);

            System.out.println("Account saved: " + savedAccount);

            // Map the saved account entity to an account DTO and return it
            return AccountMapper.mapToAccountDto(savedAccount);
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            throw e; // Re-throw the exception to be handled by the controller
        } catch (Exception e) {
            System.out.println("An error occurred while creating the account: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("An error occurred while creating the account", e); // Re-throw as a generic runtime exception
        }
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Long accountId, AccountDto accountDto) {
        try {
            Account existingAccount = accountRepository.findById(accountId)
                    .orElseThrow(() -> new UserNotFoundException("Account not found with id " + accountId));

            existingAccount.setAccountHolderName(accountDto.getAccountHolderName());


            Account updatedAccount = accountRepository.save(existingAccount);
            // Return mapped UserDto
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
    public AccountDto getAccountById(Long id) {

        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));
        return AccountMapper.mapToAccountDto(account);
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
