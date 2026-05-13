package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LoanEmiResponse {
    private int id;
    private BigDecimal emiAmount;
    private LocalDate dueDate;
    private String paidStatus;
    private LocalDateTime paidAt;
}
