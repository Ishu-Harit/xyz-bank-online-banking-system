package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BeneficiaryResponse {
    private int id;
    private String name;
    private String accountNumber;
    private String bankName;
    private LocalDateTime createdAt;
}