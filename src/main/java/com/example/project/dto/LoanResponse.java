package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LoanResponse {
    private int id;
    private String loanType;
    private BigDecimal amount;
    private int tenure;
    private BigDecimal interestRate;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
}
