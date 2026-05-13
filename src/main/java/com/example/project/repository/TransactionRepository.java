package com.example.project.repository;

import com.example.project.model.Account;
import com.example.project.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByFromAccount(Account account);
    List<Transaction> findByToAccount(Account account);
    List<Transaction> findByFromAccountOrToAccount(Account fromAccount, Account toAccount);
}