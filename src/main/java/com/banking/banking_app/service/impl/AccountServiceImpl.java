package com.banking.banking_app.service.impl;

import com.banking.banking_app.dto.AccountDto;
import com.banking.banking_app.dto.DepositDto;
import com.banking.banking_app.dto.WithdrawDto;
import com.banking.banking_app.entity.Account;
import com.banking.banking_app.entity.User;
import com.banking.banking_app.exception.UserNotFoundException;
import com.banking.banking_app.mapper.AccountMapper;
import com.banking.banking_app.repository.AccountRepository;
import com.banking.banking_app.repository.UserRepository;
import com.banking.banking_app.service.AccountService;
import com.banking.banking_app.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            existingAccount.setAccountType(accountDto.getAccountType());

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
    public AccountDto deposit(DepositDto depositDto) {
        Account account = accountRepository.findById(depositDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance() + depositDto.getAmount());
        Account updatedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(updatedAccount);
    }

    @Override
    public AccountDto withdraw(WithdrawDto withdrawDto) {
        Account account = accountRepository.findById(withdrawDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance() < withdrawDto.getAmount()) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - withdrawDto.getAmount());
        Account updatedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(updatedAccount);
    }

    @Override
    public boolean deleteAccountIfZeroBalance(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (!account.getUser().getId().equals(userId)){
            throw new RuntimeException("Unauthorized access to the account");
        }

        if (account.getBalance() == 0){
            accountRepository.delete(account);
            return true;
        }
        return false;
    }
}
