package com.banking.banking_app.controller;

import com.banking.banking_app.dto.AccountDto;
import com.banking.banking_app.dto.UserDto;
import com.banking.banking_app.entity.User;
import com.banking.banking_app.exception.UserNotFoundException;
import com.banking.banking_app.repository.AccountRepository;
import com.banking.banking_app.repository.UserRepository;
import com.banking.banking_app.service.AccountService;
import com.banking.banking_app.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // Add account API
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                String username = jwtUtil.extractUsername(jwt);

                // Verify if the token is valid
                if (username != null && jwtUtil.validateToken(jwt, userRepository.findByUsername(username))) {
                    User user = userRepository.findByUsername(username);
                    if (user == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                    }

                    // Pass the user information to the service layer
                    accountDto.setUserId(user.getId());
                    AccountDto createdAccount = accountService.createAccount(accountDto);
                    return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDto) {
        System.out.println("Received update request for user ID: " + id);
        System.out.println("Request body: " + accountDto);
        AccountDto updatedAccount = accountService.updateAccount(id, accountDto);
        System.out.println("Update successful for user ID: " + id);
        return ResponseEntity.ok(updatedAccount);
    }
}


//    // Get Account API
//    @GetMapping("/{id}")
//    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id){
//        AccountDto accountDto = accountService.getAccountById(id);
//        return ResponseEntity.ok(accountDto);
//    }
//
//    // Deposite API
//    @PutMapping("/{id}/deposit")
//    public ResponseEntity<AccountDto> deposit(@PathVariable Long id,
//                                              @RequestBody Map<String, Double> request){
//
//        Double amount = request.get("amount");
//        AccountDto accountDto = accountService.deposit(id, request.get("amount"));
//        return ResponseEntity.ok(accountDto);
//    }
//
//    // Withdraw API
//    @PutMapping("/{id}/withdraw")
//    public ResponseEntity<AccountDto> withdraw(@PathVariable Long id,
//                                              @RequestBody Map<String, Double> request){
//
//        Double amount = request.get("amount");
//        AccountDto accountDto = accountService.withdraw(id, request.get("amount"));
//        return ResponseEntity.ok(accountDto);
//    }
//
//    // Get all accounts API
//    @GetMapping
//    public ResponseEntity<List<AccountDto>> getAllAccounts(){
//        List<AccountDto> accounts = accountService.getAllAccounts();
//        return ResponseEntity.ok(accounts);
//    }
//
//    // Delete account api
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteAccount(@PathVariable Long id){
//        accountService.deleteAccount(id);
//        return ResponseEntity.ok("Account is deleted successfully!");
//    }
