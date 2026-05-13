package com.example.project.service;

import com.example.project.dto.TransactionRequest;
import com.example.project.dto.TransactionResponse;
import com.example.project.model.Account;
import com.example.project.model.Transaction;
import com.example.project.model.User;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.TransactionRepository;
import com.example.project.repository.UserRepository;
import com.example.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ─── Deposit ───────────────────────────────────────────
    @Transactional
    public TransactionResponse deposit(String token, TransactionRequest request) {
        Account account = getAccountFromToken(token);

        // Check account is active
        if (account.getStatus() != Account.Status.Active) {
            throw new RuntimeException("Account is not active!");
        }

        // Add amount to balance
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setFromAccount(null);
        transaction.setToAccount(account);
        transaction.setType(Transaction.TransactionType.Deposit);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(Transaction.Status.Success);
        transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    // ─── Withdraw ──────────────────────────────────────────
    @Transactional
    public TransactionResponse withdraw(String token, TransactionRequest request) {
        Account account = getAccountFromToken(token);

        // Check account is active
        if (account.getStatus() != Account.Status.Active) {
            throw new RuntimeException("Account is not active!");
        }

        // Check sufficient balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance!");
        }

        // Deduct amount from balance
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setFromAccount(account);
        transaction.setToAccount(account);
        transaction.setType(Transaction.TransactionType.Withdrawal);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(Transaction.Status.Success);
        transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    // ─── Transfer ──────────────────────────────────────────
    @Transactional
    public TransactionResponse transfer(String token, TransactionRequest request) {
        Account fromAccount = getAccountFromToken(token);

        // Check sender account is active
        if (fromAccount.getStatus() != Account.Status.Active) {
            throw new RuntimeException("Your account is not active!");
        }

        // Check sufficient balance
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance!");
        }

        // Find receiver account
        Account toAccount = accountRepository
                .findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Receiver account not found!"));

        // Check receiver account is active
        if (toAccount.getStatus() != Account.Status.Active) {
            throw new RuntimeException("Receiver account is not active!");
        }

        // Cannot transfer to same account
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to same account!");
        }

        // Deduct from sender
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(fromAccount);

        // Add to receiver
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(toAccount);

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setType(Transaction.TransactionType.Transfer);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(Transaction.Status.Success);
        transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    // ─── Transaction History ───────────────────────────────
    public List<TransactionResponse> getTransactionHistory(String token) {
        Account account = getAccountFromToken(token);

        List<Transaction> transactions = transactionRepository
                .findByFromAccountOrToAccount(account, account);

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─── Helper Methods ────────────────────────────────────
    private Account getAccountFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found!"));
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType().toString(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getStatus().toString(),
                transaction.getCreatedAt(),
                transaction.getFromAccount() != null ?
                        transaction.getFromAccount().getAccountNumber() : "External",
                transaction.getToAccount().getAccountNumber()
        );
    }
}