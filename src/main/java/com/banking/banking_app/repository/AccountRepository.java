package com.banking.banking_app.repository;

import com.banking.banking_app.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
    boolean existsByAccountNumber(String accountNumber);
}
