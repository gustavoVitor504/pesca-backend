package com.example.pesca.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())        // ← era setSubject
                .issuedAt(new Date())                      // ← era setIssuedAt
                .expiration(new Date(System.currentTimeMillis() + expiration)) // ← era setExpiration
                .signWith(getKey())                        // ← removeu SignatureAlgorithm
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()                               // ← era parserBuilder
                .verifyWith(getKey())                      // ← era setSigningKey
                .build()
                .parseSignedClaims(token)                  // ← era parseClaimsJws
                .getPayload()                              // ← era getBody
                .getSubject();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }
}