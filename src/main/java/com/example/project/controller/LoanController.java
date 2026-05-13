package com.example.project.controller;

import com.example.project.dto.LoanEmiResponse;
import com.example.project.dto.LoanRequest;
import com.example.project.dto.LoanResponse;
import com.example.project.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = "*")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLoan(
            @RequestHeader("Authorization") String token,
            @RequestBody LoanRequest request) {
        try {
            LoanResponse response = loanService.applyLoan(token, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyLoans(
            @RequestHeader("Authorization") String token) {
        try {
            List<LoanResponse> response = loanService.getMyLoans(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/emis/{loanId}")
    public ResponseEntity<?> getLoanEmis(
            @RequestHeader("Authorization") String token,
            @PathVariable int loanId) {
        try {
            List<LoanEmiResponse> response =
                    loanService.getLoanEmis(token, loanId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pay-emi/{emiId}")
    public ResponseEntity<?> payEmi(
            @RequestHeader("Authorization") String token,
            @PathVariable int emiId) {
        try {
            LoanEmiResponse response = loanService.payEmi(token, emiId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
