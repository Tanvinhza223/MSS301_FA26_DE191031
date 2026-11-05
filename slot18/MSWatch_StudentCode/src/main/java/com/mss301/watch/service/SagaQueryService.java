package com.mss301.watch.service;
import com.mss301.watch.dto.SagaDetails;
import com.mss301.watch.entity.SagaState;
import com.mss301.watch.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class SagaQueryService {
    private final SagaStateRepository states; private final SagaLogRepository logs;
    public SagaQueryService(SagaStateRepository states, SagaLogRepository logs) { this.states = states; this.logs = logs; }
    @Transactional(readOnly = true) public List<SagaState> recent(int limit) { return states.findAllByOrderByCreatedAtDesc(PageRequest.of(0, Math.min(Math.max(limit, 1), 100))); }
    @Transactional(readOnly = true) public SagaDetails details(String id) {
        SagaState state = states.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saga not found"));
        return new SagaDetails(state, logs.findBySagaIdOrderBySequenceAsc(id));
    }
}

