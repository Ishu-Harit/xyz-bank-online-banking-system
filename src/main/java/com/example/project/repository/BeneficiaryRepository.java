package com.example.project.repository;

import com.example.project.model.Beneficiary;
import com.example.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Integer> {
    List<Beneficiary> findByUser(User user);
    boolean existsByUserAndAccountNumber(User user, String accountNumber);
}