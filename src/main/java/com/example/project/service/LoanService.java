package com.example.project.service;

import com.example.project.dto.LoanEmiResponse;
import com.example.project.dto.LoanRequest;
import com.example.project.dto.LoanResponse;
import com.example.project.model.Loan;
import com.example.project.model.LoanEmi;
import com.example.project.model.User;
import com.example.project.repository.LoanEmiRepository;
import com.example.project.repository.LoanRepository;
import com.example.project.repository.UserRepository;
import com.example.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanEmiRepository loanEmiRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ─── Apply for Loan ────────────────────────────────────
    public LoanResponse applyLoan(String token, LoanRequest request) {
        User user = getUserFromToken(token);

        // Set interest rate based on loan type
        BigDecimal interestRate = getInterestRate(request.getLoanType());

        // Create loan
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setLoanType(Loan.LoanType.valueOf(request.getLoanType()));
        loan.setAmount(request.getAmount());
        loan.setTenure(request.getTenure());
        loan.setInterestRate(interestRate);
        loan.setStatus(Loan.Status.Pending);

        Loan savedLoan = loanRepository.save(loan);

// Generate EMIs after saving loan
        generateEmis(savedLoan);

        return mapLoanToResponse(savedLoan);
    }

    // ─── Get All Loans of User ─────────────────────────────
    public List<LoanResponse> getMyLoans(String token) {
        User user = getUserFromToken(token);
        List<Loan> loans = loanRepository.findByUser(user);
        return loans.stream()
                .map(this::mapLoanToResponse)
                .collect(Collectors.toList());
    }

    // ─── Get Loan EMIs ─────────────────────────────────────
    public List<LoanEmiResponse> getLoanEmis(String token, int loanId) {
        User user = getUserFromToken(token);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found!"));

        // Check loan belongs to this user
        if (loan.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized action!");
        }

        List<LoanEmi> emis = loanEmiRepository.findByLoan(loan);
        return emis.stream()
                .map(this::mapEmiToResponse)
                .collect(Collectors.toList());
    }

    // ─── Pay EMI ───────────────────────────────────────────
    public LoanEmiResponse payEmi(String token, int emiId) {
        User user = getUserFromToken(token);
        LoanEmi emi = loanEmiRepository.findById(emiId)
                .orElseThrow(() -> new RuntimeException("EMI not found!"));

        // Check EMI belongs to this user's loan
        if (emi.getLoan().getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized action!");
        }

        // Check EMI not already paid
        if (emi.getPaidStatus() == LoanEmi.PaidStatus.Paid) {
            throw new RuntimeException("EMI already paid!");
        }

        // Mark as paid
        emi.setPaidStatus(LoanEmi.PaidStatus.Paid);
        emi.setPaidAt(LocalDateTime.now());
        loanEmiRepository.save(emi);

        return mapEmiToResponse(emi);
    }

    // ─── Helper Methods ────────────────────────────────────
    private BigDecimal getInterestRate(String loanType) {
        switch (loanType) {
            case "Home":       return new BigDecimal("8.5");
            case "Personal":   return new BigDecimal("12.0");
            case "Education":  return new BigDecimal("7.0");
            default:           return new BigDecimal("10.0");
        }
    }

    private BigDecimal calculateEmi(BigDecimal principal,
                                    BigDecimal annualRate,
                                    int tenureMonths) {
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowN = onePlusR.pow(tenureMonths);

        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowN);
        BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private void generateEmis(Loan loan) {
        BigDecimal emiAmount = calculateEmi(
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getTenure()
        );

        for (int i = 1; i <= loan.getTenure(); i++) {
            LoanEmi emi = new LoanEmi();
            emi.setLoan(loan);
            emi.setEmiAmount(emiAmount);
            emi.setDueDate(LocalDate.now().plusMonths(i));
            emi.setPaidStatus(LoanEmi.PaidStatus.Unpaid);
            loanEmiRepository.save(emi);
        }
    }

    private User getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    private LoanResponse mapLoanToResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getLoanType().toString(),
                loan.getAmount(),
                loan.getTenure(),
                loan.getInterestRate(),
                loan.getStatus().toString(),
                loan.getAppliedAt(),
                loan.getApprovedAt()
        );
    }

    private LoanEmiResponse mapEmiToResponse(LoanEmi emi) {
        return new LoanEmiResponse(
                emi.getId(),
                emi.getEmiAmount(),
                emi.getDueDate(),
                emi.getPaidStatus().toString(),
                emi.getPaidAt()
        );
    }
}
