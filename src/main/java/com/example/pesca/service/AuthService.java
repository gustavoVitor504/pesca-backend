package com.example.pesca.service;

import com.example.pesca.dto.AuthDTO.*;
import com.example.pesca.model.User;
import com.example.pesca.repository.UserRepository;
import com.example.pesca.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado.");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .emailVerified(false)
                .verificationToken(verificationToken)
                .build();

        userRepository.save(user);

        emailService.enviarVerificacaoEmail(user.getEmail(), verificationToken);

        // Não retorna token — usuário precisa verificar email primeiro
        return new AuthResponse(null, user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!user.isEmailVerified()) {
            throw new BadCredentialsException("EMAIL_NOT_VERIFIED");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }

    public void verificarEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado."));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
}