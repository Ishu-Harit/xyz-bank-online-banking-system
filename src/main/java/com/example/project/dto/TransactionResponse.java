package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private int id;
    private String type;
    private BigDecimal amount;
    private String description;
    private String status;
    private LocalDateTime transactionDate;
    private String fromAccount;
    private String toAccount;
}
