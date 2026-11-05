package com.mss301.watch.controller;

import com.mss301.watch.exception.SagaExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(SagaExecutionException.class)
    ResponseEntity<Map<String, Object>> saga(SagaExecutionException exception) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sagaId", exception.getSagaId()); data.put("sagaStatus", exception.getSagaStatus());
        data.put("failedStep", exception.getFailedStep()); data.put("reason", exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).header("X-Saga-Id", exception.getSagaId())
            .body(Map.of("success", false, "message", exception.getMessage(), "data", data));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Validation failed", "errors", errors));
    }
}

