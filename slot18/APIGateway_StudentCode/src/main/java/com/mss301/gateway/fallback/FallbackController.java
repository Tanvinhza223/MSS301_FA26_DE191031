package com.mss301.gateway.fallback;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @RequestMapping("/account") ResponseEntity<Map<String, Object>> account() { return unavailable("Account Service"); }
    @RequestMapping("/brand") ResponseEntity<Map<String, Object>> brand() { return unavailable("Brand Service"); }
    @RequestMapping("/watch") ResponseEntity<Map<String, Object>> watch() { return unavailable("Watch Service"); }
    private ResponseEntity<Map<String, Object>> unavailable(String service) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
            "success", false, "message", service + " is currently unavailable. Please try again later.",
            "timestamp", LocalDateTime.now().toString()));
    }
}

