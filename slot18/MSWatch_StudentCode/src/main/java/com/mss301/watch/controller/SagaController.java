package com.mss301.watch.controller;
import com.mss301.watch.dto.SagaDetails;
import com.mss301.watch.entity.SagaState;
import com.mss301.watch.service.SagaQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/sagas")
public class SagaController {
    private final SagaQueryService sagas;
    public SagaController(SagaQueryService sagas) { this.sagas = sagas; }
    @GetMapping List<SagaState> recent(@RequestHeader("X-User-Role") Integer role, @RequestParam(defaultValue = "20") int limit) { admin(role); return sagas.recent(limit); }
    @GetMapping("/{id}") SagaDetails details(@RequestHeader("X-User-Role") Integer role, @PathVariable String id) { admin(role); return sagas.details(id); }
    private void admin(Integer role) { if (!Integer.valueOf(1).equals(role)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator role required"); }
}

