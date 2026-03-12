package com.example.pesca.controller;

import com.example.pesca.dto.PaymentDTO;
import com.example.pesca.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO.Request request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }
}