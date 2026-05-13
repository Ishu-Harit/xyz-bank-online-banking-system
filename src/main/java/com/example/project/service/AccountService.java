package com.example.project.service;

import com.example.project.dto.AccountResponse;
import com.example.project.model.Account;
import com.example.project.model.User;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.UserRepository;
import com.example.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public AccountResponse getAccountDetails(String token) {
        String email = extractEmail(token);
        User user = getUser(email);
        Account account = getAccount(user);

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType().toString(),
                account.getBalance(),
                account.getStatus().toString(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    public BigDecimal getBalance(String token) {
        String email = extractEmail(token);
        User user = getUser(email);
        Account account = getAccount(user);
        return account.getBalance();
    }

    private String extractEmail(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.extractEmail(token);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    private Account getAccount(User user) {
        return accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found!"));
    }
}
