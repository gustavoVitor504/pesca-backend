package com.example.pesca.controller;

import com.example.pesca.dto.OrderDTO.*;
import com.example.pesca.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateRequest request) {
        return ResponseEntity.ok(orderService.create(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> myOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.findByUser(userDetails.getUsername()));
    }
}
