package com.example.project.controller;

import com.example.project.dto.AccountResponse;
import com.example.project.dto.AdminLoanUpdateRequest;
import com.example.project.dto.AdminUserUpdateRequest;
import com.example.project.dto.LoanResponse;
import com.example.project.model.User;
import com.example.project.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("Authorization") String token) {
        try {
            List<User> users = adminService.getAllUsers(token);
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/user/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable int userId,
            @RequestBody AdminUserUpdateRequest request) {
        try {
            String response = adminService.updateUserStatus(token, userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts(
            @RequestHeader("Authorization") String token) {
        try {
            List<AccountResponse> response = adminService.getAllAccounts(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/account/{accountId}/status")
    public ResponseEntity<?> updateAccountStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable int accountId,
            @RequestParam String status) {
        try {
            String response = adminService.updateAccountStatus(
                    token, accountId, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/loans")
    public ResponseEntity<?> getAllLoans(
            @RequestHeader("Authorization") String token) {
        try {
            List<LoanResponse> response = adminService.getAllLoans(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/loans/pending")
    public ResponseEntity<?> getPendingLoans(
            @RequestHeader("Authorization") String token) {
        try {
            List<LoanResponse> response = adminService.getPendingLoans(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/loan/{loanId}/status")
    public ResponseEntity<?> updateLoanStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable int loanId,
            @RequestBody AdminLoanUpdateRequest request) {
        try {
            String response = adminService.updateLoanStatus(
                    token, loanId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}