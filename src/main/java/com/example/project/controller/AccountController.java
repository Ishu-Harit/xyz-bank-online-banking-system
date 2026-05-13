package com.example.project.controller;

import com.example.project.dto.AccountResponse;
import com.example.project.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/details")
    public ResponseEntity<?> getAccountDetails(
            @RequestHeader("Authorization") String token) {
        try {
            AccountResponse response = accountService.getAccountDetails(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(
            @RequestHeader("Authorization") String token) {
        try {
            BigDecimal balance = accountService.getBalance(token);
            return ResponseEntity.ok(balance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
