package com.example.project.dto;

import lombok.Data;

@Data
public class BeneficiaryRequest {
    private String name;
    private String accountNumber;
    private String bankName;
}
