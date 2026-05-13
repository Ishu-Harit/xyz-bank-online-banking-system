package com.example.project.service;

import com.example.project.dto.AuthResponse;
import com.example.project.dto.LoginRequest;
import com.example.project.dto.RegisterRequest;
import com.example.project.model.Account;
import com.example.project.model.User;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.UserRepository;
import com.example.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // Check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already registered!");
        }

        // Create new User
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(User.Role.User);
        user.setStatus(User.Status.Active);

        // Save user to DB
        User savedUser = userRepository.save(user);

        // Create Account for user
        Account account = new Account();
        account.setUser(savedUser);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(Account.AccountType.valueOf(request.getAccountType()));
        account.setBalance(java.math.BigDecimal.ZERO);
        account.setStatus(Account.Status.Active);

        // Save account to DB
        Account savedAccount = accountRepository.save(account);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail());

        // Return response
        return new AuthResponse(
                token,
                savedUser.getName(),
                savedUser.getEmail(),
                savedAccount.getAccountNumber(),
                savedAccount.getAccountType().toString()
        );
    }

    public AuthResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if account is active
        if (user.getStatus() == User.Status.Inactive) {
            throw new RuntimeException("Account is inactive!");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        // Get account details
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Return response
        return new AuthResponse(
                token,
                user.getName(),
                user.getEmail(),
                account.getAccountNumber(),
                account.getAccountType().toString()
        );
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }
}
