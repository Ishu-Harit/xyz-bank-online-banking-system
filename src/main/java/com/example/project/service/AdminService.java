package com.example.project.service;

import com.example.project.dto.AccountResponse;
import com.example.project.dto.AdminLoanUpdateRequest;
import com.example.project.dto.AdminUserUpdateRequest;
import com.example.project.dto.LoanResponse;
import com.example.project.model.Account;
import com.example.project.model.Loan;
import com.example.project.model.LoanEmi;
import com.example.project.model.User;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.LoanEmiRepository;
import com.example.project.repository.LoanRepository;
import com.example.project.repository.UserRepository;
import com.example.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanEmiRepository loanEmiRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ─── Check Admin ───────────────────────────────────────
    private User getAdminFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        if (user.getRole() != User.Role.Admin) {
            throw new RuntimeException("Unauthorized! Admin access only!");
        }
        return user;
    }

    // ─── Get All Users ─────────────────────────────────────
    public List<User> getAllUsers(String token) {
        getAdminFromToken(token);
        return userRepository.findAll();
    }

    // ─── Update User Status ────────────────────────────────
    public String updateUserStatus(String token, int userId,
                                   AdminUserUpdateRequest request) {
        getAdminFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setStatus(User.Status.valueOf(request.getStatus()));
        userRepository.save(user);
        return "User status updated to " + request.getStatus();
    }

    // ─── Get All Accounts ──────────────────────────────────
    public List<AccountResponse> getAllAccounts(String token) {
        getAdminFromToken(token);
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getAccountType().toString(),
                        account.getBalance(),
                        account.getStatus().toString(),
                        account.getUser().getName(),
                        account.getUser().getEmail(),
                        account.getUser().getPhone()
                ))
                .collect(Collectors.toList());
    }

    // ─── Freeze / Unfreeze Account ─────────────────────────
    public String updateAccountStatus(String token, int accountId,
                                      String status) {
        getAdminFromToken(token);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found!"));

        account.setStatus(Account.Status.valueOf(status));
        accountRepository.save(account);
        return "Account status updated to " + status;
    }

    // ─── Get All Loans ─────────────────────────────────────
    public List<LoanResponse> getAllLoans(String token) {
        getAdminFromToken(token);
        List<Loan> loans = loanRepository.findAll();
        return loans.stream()
                .map(loan -> new LoanResponse(
                        loan.getId(),
                        loan.getLoanType().toString(),
                        loan.getAmount(),
                        loan.getTenure(),
                        loan.getInterestRate(),
                        loan.getStatus().toString(),
                        loan.getAppliedAt(),
                        loan.getApprovedAt()
                ))
                .collect(Collectors.toList());
    }

    // ─── Approve / Reject Loan ─────────────────────────────
    public String updateLoanStatus(String token, int loanId,
                                   AdminLoanUpdateRequest request) {
        getAdminFromToken(token);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found!"));

        Loan.Status newStatus = Loan.Status.valueOf(request.getStatus());
        loan.setStatus(newStatus);

        if (newStatus == Loan.Status.Approved) {
            loan.setApprovedAt(LocalDateTime.now());
            generateEmis(loan);
        }

        loanRepository.save(loan);
        return "Loan status updated to " + request.getStatus();
    }

    // ─── Generate EMIs on Approval ─────────────────────────
    private void generateEmis(Loan loan) {
        // Clear existing EMIs first
        List<LoanEmi> existingEmis = loanEmiRepository.findByLoan(loan);
        loanEmiRepository.deleteAll(existingEmis);

        // Calculate EMI amount
        java.math.BigDecimal monthlyRate = loan.getInterestRate()
                .divide(java.math.BigDecimal.valueOf(1200), 10,
                        java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal onePlusR =
                java.math.BigDecimal.ONE.add(monthlyRate);
        java.math.BigDecimal onePlusRPowN = onePlusR.pow(loan.getTenure());
        java.math.BigDecimal numerator = loan.getAmount()
                .multiply(monthlyRate).multiply(onePlusRPowN);
        java.math.BigDecimal denominator =
                onePlusRPowN.subtract(java.math.BigDecimal.ONE);
        java.math.BigDecimal emiAmount = numerator.divide(
                denominator, 2, java.math.RoundingMode.HALF_UP);

        // Generate EMI schedule
        for (int i = 1; i <= loan.getTenure(); i++) {
            LoanEmi emi = new LoanEmi();
            emi.setLoan(loan);
            emi.setEmiAmount(emiAmount);
            emi.setDueDate(java.time.LocalDate.now().plusMonths(i));
            emi.setPaidStatus(LoanEmi.PaidStatus.Unpaid);
            loanEmiRepository.save(emi);
        }
    }

    // ─── Get All Pending Loans ─────────────────────────────
    public List<LoanResponse> getPendingLoans(String token) {
        getAdminFromToken(token);
        List<Loan> loans = loanRepository.findByStatus(Loan.Status.Pending);
        return loans.stream()
                .map(loan -> new LoanResponse(
                        loan.getId(),
                        loan.getLoanType().toString(),
                        loan.getAmount(),
                        loan.getTenure(),
                        loan.getInterestRate(),
                        loan.getStatus().toString(),
                        loan.getAppliedAt(),
                        loan.getApprovedAt()
                ))
                .collect(Collectors.toList());
    }
}
