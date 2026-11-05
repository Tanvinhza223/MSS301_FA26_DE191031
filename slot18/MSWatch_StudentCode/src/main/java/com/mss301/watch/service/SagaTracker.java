package com.mss301.watch.service;

import com.mss301.watch.entity.*;
import com.mss301.watch.repository.*;
import com.mss301.watch.saga.SagaStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SagaTracker {
    private final SagaStateRepository states;
    private final SagaLogRepository logs;
    public SagaTracker(SagaStateRepository states, SagaLogRepository logs) { this.states = states; this.logs = logs; }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void start(String id, String type) { states.save(new SagaState(id, type)); }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transition(String id, SagaStatus status, String step, String failedStep, String reason) {
        SagaState state = states.findById(id).orElseThrow();
        state.transition(status, step, failedStep, reason);
        states.save(state);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String id, String step, String action, String status, String details) {
        int sequence = Math.toIntExact(logs.countBySagaId(id) + 1);
        logs.save(new SagaLog(id, sequence, step, action, status, details));
    }
}

