package com.mss301.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {
    static final String USER_ID = "X-User-Id";
    static final String USER_ROLE = "X-User-Role";
    private static final List<String> PUBLIC_PREFIXES = List.of("/api/auth/login", "/api/auth/refresh", "/api/auth/logout", "/fallback/");
    @Value("${jwt.secret}") private String secret;

    @Override public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest original = exchange.getRequest();
        String path = original.getURI().getPath();
        ServerHttpRequest sanitized = original.mutate().headers(headers -> {
            headers.remove(USER_ID); headers.remove(USER_ROLE);
        }).build();
        boolean publicRequest = PUBLIC_PREFIXES.stream().anyMatch(path::startsWith)
            || (HttpMethod.GET.equals(original.getMethod()) && "/api/watches".equals(path));
        if (publicRequest) return chain.filter(exchange.mutate().request(sanitized).build());

        String authorization = original.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) return reject(exchange, HttpStatus.UNAUTHORIZED);
        try {
            Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build().parseSignedClaims(authorization.substring(7)).getPayload();
            if (!"access".equals(claims.get("type", String.class))) return reject(exchange, HttpStatus.UNAUTHORIZED);
            Integer role = claims.get("role", Integer.class);
            ServerHttpRequest authenticated = sanitized.mutate().header(USER_ID, claims.getSubject())
                .header(USER_ROLE, String.valueOf(role)).build();
            return chain.filter(exchange.mutate().request(authenticated).build());
        } catch (Exception exception) {
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
    @Override public int getOrder() { return -1; }
}
