package com.mss301.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthFilterTest {
    @Test void publicWatchListStripsSpoofedHeaders() throws Exception {
        JwtAuthFilter filter = filter();
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/watches")
            .header("X-User-Role", "1").build());
        AtomicReference<HttpHeaders> downstream = new AtomicReference<>();
        StepVerifier.create(filter.filter(exchange, current -> { downstream.set(current.getRequest().getHeaders()); return Mono.empty(); })).verifyComplete();
        assertThat(downstream.get().get("X-User-Role")).isNull();
    }

    @Test void postWatchRequiresToken() throws Exception {
        JwtAuthFilter filter = filter();
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/watches").build());
        StepVerifier.create(filter.filter(exchange, current -> Mono.empty())).verifyComplete();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private JwtAuthFilter filter() throws Exception {
        JwtAuthFilter filter = new JwtAuthFilter();
        Field secret = JwtAuthFilter.class.getDeclaredField("secret"); secret.setAccessible(true);
        secret.set(filter, "mss301-watch-management-secret-key-2025-very-long-string");
        return filter;
    }
}
