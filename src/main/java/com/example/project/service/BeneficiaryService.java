package com.example.project.service;

import com.example.project.dto.BeneficiaryRequest;
import com.example.project.dto.BeneficiaryResponse;
import com.example.project.model.Account;
import com.example.project.model.Beneficiary;
import com.example.project.model.User;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.BeneficiaryRepository;
import com.example.project.repository.UserRepository;
import com.example.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public BeneficiaryResponse addBeneficiary(String token, BeneficiaryRequest request) {
        User user = getUserFromToken(token);

        // Check if beneficiary already added
        if (beneficiaryRepository.existsByUserAndAccountNumber(
                user, request.getAccountNumber())) {
            throw new RuntimeException("Beneficiary already added!");
        }

        // Check cannot add own account
        Account userAccount = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found!"));
        if (userAccount.getAccountNumber().equals(request.getAccountNumber())) {
            throw new RuntimeException("Cannot add your own account as beneficiary!");
        }

        // Create beneficiary
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(user);
        beneficiary.setName(request.getName());
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setBankName(request.getBankName());

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return mapToResponse(saved);
    }

    public List<BeneficiaryResponse> getAllBeneficiaries(String token) {
        User user = getUserFromToken(token);
        List<Beneficiary> beneficiaries = beneficiaryRepository.findByUser(user);
        return beneficiaries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public String deleteBeneficiary(String token, int beneficiaryId) {
        User user = getUserFromToken(token);
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found!"));

        // Check beneficiary belongs to this user
        if (beneficiary.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized action!");
        }

        beneficiaryRepository.delete(beneficiary);
        return "Beneficiary deleted successfully!";
    }

    private User getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    private BeneficiaryResponse mapToResponse(Beneficiary beneficiary) {
        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getName(),
                beneficiary.getAccountNumber(),
                beneficiary.getBankName(),
                beneficiary.getCreatedAt()
        );
    }
}
