package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountResponse {
    private int id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private String userName;
    private String userEmail;
    private String userPhone;
}
