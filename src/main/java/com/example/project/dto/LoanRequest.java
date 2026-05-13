package com.example.project.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanRequest {
    private String loanType;
    private BigDecimal amount;
    private int tenure;
}
