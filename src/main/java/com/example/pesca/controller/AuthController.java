package com.example.pesca.controller;

import com.example.pesca.dto.AuthDTO.*;
import com.example.pesca.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Value("${app.frontend-url}")
    private String frontendUrl;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @GetMapping("/verify")
    public ResponseEntity<Void> verify(@RequestParam String token) {
        authService.verificarEmail(token);
        // Redireciona para o frontend após verificar
        return ResponseEntity.status(302)
                .header("Location", frontendUrl + "/verificar-email?success=true")
                .build();
    }
}
