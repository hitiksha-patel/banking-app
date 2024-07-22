package com.banking.banking_app.controller;

import com.banking.banking_app.dto.AccountDto;
import com.banking.banking_app.dto.DepositDto;
import com.banking.banking_app.dto.WithdrawDto;
import com.banking.banking_app.entity.User;
import com.banking.banking_app.repository.UserRepository;
import com.banking.banking_app.service.AccountService;
import com.banking.banking_app.util.AuthUtil;
import com.banking.banking_app.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtil authUtil;

    // CREATE ACCOUNT API
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        try {
            User user = authUtil.getAuthenticatedUser();
            accountDto.setUserId(user.getId());
            AccountDto createdAccount = accountService.createAccount(accountDto);
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDto) {
        AccountDto updatedAccount = accountService.updateAccount(id, accountDto);
        return ResponseEntity.ok(updatedAccount);
    }

    @GetMapping("/my-accounts")
    public ResponseEntity<List<AccountDto>> getMyAccounts() {
        try {
            User user = authUtil.getAuthenticatedUser();
            List<AccountDto> accounts = accountService.getAccountsByUserId(user.getId());
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<AccountDto> deposit(@RequestBody DepositDto depositDto, HttpServletRequest request) {
        try {
            User user = authUtil.getAuthenticatedUser();
            AccountDto updatedAccount = accountService.deposit(depositDto);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountDto> withdraw(@RequestBody WithdrawDto withdrawDto) {
        try {
            User user = authUtil.getAuthenticatedUser();
            AccountDto updatedAccount = accountService.withdraw(withdrawDto);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId){
        try{
            User user = authUtil.getAuthenticatedUser();
            boolean isDeleted = accountService.deleteAccountIfZeroBalance(accountId, user.getId());
            if (isDeleted){
                return new ResponseEntity<>("Account deleted successfully", HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Account balance is not zero. Please withdraw all funds before deleting the account.", HttpStatus.BAD_REQUEST);
            }
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

