package com.mss301.account.service;

import com.mss301.account.dto.LoginRequest;
import com.mss301.account.dto.LoginResponse;
import com.mss301.account.entity.StoreAccount;
import com.mss301.account.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final AccountRepository accounts;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(AccountRepository accounts, PasswordEncoder encoder, JwtService jwt) {
        this.accounts = accounts;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public LoginResponse login(LoginRequest request) {
        StoreAccount account = accounts.findByUsername(request.username())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (!account.getActive()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is inactive");
        if (!encoder.matches(request.password(), account.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        return response(account);
    }

    public LoginResponse refresh(String token) {
        try {
            var claims = jwt.parse(token);
            if (!"refresh".equals(claims.get("type", String.class)))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is not a refresh token");
            StoreAccount account = accounts.findById(Integer.valueOf(claims.getSubject()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found"));
            if (!account.getActive()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is inactive");
            return response(account);
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }
    }

    private LoginResponse response(StoreAccount account) {
        String subject = account.getAccountId().toString();
        return new LoginResponse(jwt.generateAccessToken(subject, account.getRole()), jwt.generateRefreshToken(subject),
            "Bearer", account.getRole(), account.getUsername());
    }
}

