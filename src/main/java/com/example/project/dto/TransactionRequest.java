package com.example.project.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String toAccountNumber;
    private BigDecimal amount;
    private String description;
}