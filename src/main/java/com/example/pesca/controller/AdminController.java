package com.example.pesca.controller;

import com.example.pesca.dto.AdminDTO;
import com.example.pesca.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ===== PRODUTOS =====
    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(adminService.getAllProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody AdminDTO.ProductRequest request) {
        return ResponseEntity.ok(adminService.createProduct(request));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody AdminDTO.ProductRequest request) {
        return ResponseEntity.ok(adminService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ===== PEDIDOS =====
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody AdminDTO.StatusRequest request) {
        return ResponseEntity.ok(adminService.updateOrderStatus(id, request.getStatus()));
    }

    // ===== DASHBOARD =====
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}