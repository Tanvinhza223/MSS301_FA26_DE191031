package com.mss301.watch.controller;

import com.mss301.watch.dto.*;
import com.mss301.watch.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/watches")
public class WatchController {
    private final WatchQueryService query; private final WatchSagaOrchestrator saga;
    public WatchController(WatchQueryService query, WatchSagaOrchestrator saga) { this.query = query; this.saga = saga; }
    @GetMapping public List<WatchResponse> findAll() { return query.findAll(); }

    @PostMapping
    ResponseEntity<SagaResponse<WatchResponse>> create(@RequestHeader("X-User-Role") Integer role,
        @RequestHeader(value = "X-Saga-Fail-After-Step", required = false) Integer failAfter,
        @Valid @RequestBody WatchRequest request) {
        requireAdmin(role); query.validateReleaseDate(request.releaseDate());
        SagaResponse<WatchResponse> result = saga.create(request, failAfter);
        return ResponseEntity.status(HttpStatus.CREATED).header("X-Saga-Id", result.sagaId()).body(result);
    }
    @PutMapping("/{id}")
    ResponseEntity<SagaResponse<WatchResponse>> update(@PathVariable Integer id, @RequestHeader("X-User-Role") Integer role,
        @RequestHeader(value = "X-Saga-Fail-After-Step", required = false) Integer failAfter,
        @Valid @RequestBody WatchRequest request) {
        requireAdmin(role); query.validateReleaseDate(request.releaseDate());
        SagaResponse<WatchResponse> result = saga.update(id, request, failAfter);
        return ResponseEntity.ok().header("X-Saga-Id", result.sagaId()).body(result);
    }
    @DeleteMapping("/{id}")
    ResponseEntity<SagaResponse<Void>> delete(@PathVariable Integer id, @RequestHeader("X-User-Role") Integer role,
        @RequestHeader(value = "X-Saga-Fail-After-Step", required = false) Integer failAfter) {
        requireAdmin(role); SagaResponse<Void> result = saga.delete(id, failAfter);
        return ResponseEntity.ok().header("X-Saga-Id", result.sagaId()).body(result);
    }
    private void requireAdmin(Integer role) { if (!Integer.valueOf(1).equals(role)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator role required"); }
}

