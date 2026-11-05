package com.mss301.account.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration}") private long expiration;
    @Value("${jwt.refresh-expiration}") private long refreshExpiration;

    public String generateAccessToken(String subject, Integer role) {
        return build(subject, Map.of("role", role, "type", "access"), expiration);
    }

    public String generateRefreshToken(String subject) {
        return build(subject, Map.of("type", "refresh"), refreshExpiration);
    }

    private String build(String subject, Map<String, Object> claims, long lifetime) {
        return Jwts.builder().subject(subject).claims(claims).issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + lifetime))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
            .build().parseSignedClaims(token).getPayload();
    }
}

