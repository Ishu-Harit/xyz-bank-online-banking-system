package com.example.project.repository;

import com.example.project.model.Loan;
import com.example.project.model.LoanEmi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanEmiRepository extends JpaRepository<LoanEmi, Integer> {
    List<LoanEmi> findByLoan(Loan loan);
    List<LoanEmi> findByPaidStatus(LoanEmi.PaidStatus paidStatus);
}