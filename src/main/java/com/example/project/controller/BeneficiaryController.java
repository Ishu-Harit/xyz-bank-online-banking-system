package com.example.project.controller;

import com.example.project.dto.BeneficiaryRequest;
import com.example.project.dto.BeneficiaryResponse;
import com.example.project.service.BeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiary")
@CrossOrigin(origins = "*")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @PostMapping("/add")
    public ResponseEntity<?> addBeneficiary(
            @RequestHeader("Authorization") String token,
            @RequestBody BeneficiaryRequest request) {
        try {
            BeneficiaryResponse response =
                    beneficiaryService.addBeneficiary(token, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBeneficiaries(
            @RequestHeader("Authorization") String token) {
        try {
            List<BeneficiaryResponse> response =
                    beneficiaryService.getAllBeneficiaries(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBeneficiary(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        try {
            String response = beneficiaryService.deleteBeneficiary(token, id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}