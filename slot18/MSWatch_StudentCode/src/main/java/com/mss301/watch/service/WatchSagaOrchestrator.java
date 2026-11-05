package com.mss301.watch.service;

import com.mss301.watch.client.BrandClient;
import com.mss301.watch.dto.*;
import com.mss301.watch.entity.Watch;
import com.mss301.watch.exception.SagaExecutionException;
import com.mss301.watch.saga.*;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class WatchSagaOrchestrator {
    private final WatchLocalService local;
    private final BrandClient brands;
    private final SagaTracker tracker;
    @Value("${saga.fault-injection.enabled:false}") private boolean faultEnabled;
    @Value("${saga.compensation.max-attempts:3}") private int maxAttempts;

    public WatchSagaOrchestrator(WatchLocalService local, BrandClient brands, SagaTracker tracker) {
        this.local = local; this.brands = brands; this.tracker = tracker;
    }

    public SagaResponse<WatchResponse> create(WatchRequest request, Integer failAfter) {
        String sagaId = start("CREATE_WATCH");
        Deque<Compensation> compensations = new ArrayDeque<>();
        String step = "CREATE_WATCH";
        try {
            Watch watch = local.create(request);
            tracker.log(sagaId, step, "FORWARD", "COMPLETED", "Watch " + watch.getWatchId() + " created");
            compensations.push(new Compensation("DELETE_WATCH", () -> local.deleteIfExists(watch.getWatchId())));
            inject(failAfter, 1);
            step = "ASSIGN_BRAND";
            brands.assign(request.brandId(), watch.getWatchId());
            tracker.log(sagaId, step, "FORWARD", "COMPLETED", "Brand " + request.brandId() + " assigned");
            compensations.push(new Compensation("UNASSIGN_BRAND", () -> brands.unassign(watch.getWatchId())));
            inject(failAfter, 2);
            complete(sagaId, step);
            return new SagaResponse<>(sagaId, SagaStatus.COMPLETED, WatchMapper.toResponse(watch));
        } catch (Exception exception) {
            throw compensate(sagaId, step, compensations, exception);
        }
    }

    public SagaResponse<WatchResponse> update(Integer id, WatchRequest request, Integer failAfter) {
        WatchData before = local.snapshot(id);
        String sagaId = start("UPDATE_WATCH");
        Deque<Compensation> compensations = new ArrayDeque<>();
        String step = "UPDATE_WATCH";
        try {
            Watch watch = local.update(id, request);
            tracker.log(sagaId, step, "FORWARD", "COMPLETED", "Watch " + id + " updated");
            compensations.push(new Compensation("RESTORE_WATCH", () -> local.restore(before)));
            inject(failAfter, 1);
            step = "ASSIGN_BRAND";
            brands.assign(request.brandId(), id);
            tracker.log(sagaId, step, "FORWARD", "COMPLETED", "Brand " + request.brandId() + " assigned");
            compensations.push(new Compensation("ASSIGN_BRAND_BACK", () -> brands.assign(before.brandId(), id)));
            inject(failAfter, 2);
            complete(sagaId, step);
            return new SagaResponse<>(sagaId, SagaStatus.COMPLETED, WatchMapper.toResponse(watch));
        } catch (Exception exception) {
            throw compensate(sagaId, step, compensations, exception);
        }
    }

    public SagaResponse<Void> delete(Integer id, Integer failAfter) {
        WatchData before = local.snapshot(id);
        String sagaId = start("DELETE_WATCH");
        Deque<Compensation> compensations = new ArrayDeque<>();
        String step = "UNASSIGN_BRAND";
        boolean pivotCommitted = false;
        try {
            brands.unassign(id);
            tracker.log(sagaId, step, "FORWARD", "COMPLETED", "Brand link removed");
            compensations.push(new Compensation("ASSIGN_BRAND_BACK", () -> brands.assign(before.brandId(), id)));
            inject(failAfter, 1);
            step = "DELETE_WATCH";
            local.delete(id);
            pivotCommitted = true;
            tracker.log(sagaId, step, "PIVOT", "COMPLETED", "Watch " + id + " deleted");
            if (faultEnabled && Objects.equals(failAfter, 2))
                tracker.log(sagaId, step, "FAULT", "SKIPPED", "Fault ignored after pivot");
            complete(sagaId, step);
            return new SagaResponse<>(sagaId, SagaStatus.COMPLETED, null);
        } catch (Exception exception) {
            if (pivotCommitted) {
                tracker.transition(sagaId, SagaStatus.COMPLETED, step, null, null);
                return new SagaResponse<>(sagaId, SagaStatus.COMPLETED, null);
            }
            throw compensate(sagaId, step, compensations, exception);
        }
    }

    private String start(String type) {
        String id = UUID.randomUUID().toString();
        tracker.start(id, type); tracker.transition(id, SagaStatus.EXECUTING, null, null, null); return id;
    }
    private void complete(String id, String step) { tracker.transition(id, SagaStatus.COMPLETED, step, null, null); }
    private void inject(Integer requested, int completedStep) {
        if (faultEnabled && Objects.equals(requested, completedStep)) throw new IllegalStateException("Injected failure after step " + completedStep);
    }
    private SagaExecutionException compensate(String id, String failedStep, Deque<Compensation> stack, Exception cause) {
        String reason = reason(cause);
        tracker.log(id, failedStep, "FORWARD", "FAILED", reason);
        tracker.transition(id, SagaStatus.FAILED, failedStep, failedStep, reason);
        tracker.transition(id, SagaStatus.COMPENSATING, failedStep, failedStep, reason);
        try {
            while (!stack.isEmpty()) runCompensation(id, stack.pop());
            tracker.transition(id, SagaStatus.COMPENSATED, failedStep, failedStep, reason);
            return new SagaExecutionException("Saga COMPENSATED: " + reason, id, SagaStatus.COMPENSATED, failedStep, status(cause), cause);
        } catch (RuntimeException compensationFailure) {
            String combined = reason + "; compensation failed: " + reason(compensationFailure);
            tracker.transition(id, SagaStatus.COMPENSATING, failedStep, failedStep, combined);
            return new SagaExecutionException("Saga requires manual recovery: " + combined, id, SagaStatus.COMPENSATING, failedStep,
                HttpStatus.SERVICE_UNAVAILABLE, compensationFailure);
        }
    }
    private void runCompensation(String id, Compensation compensation) {
        RuntimeException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                compensation.action().run();
                tracker.log(id, compensation.stepName(), "COMPENSATE", "COMPLETED", "Attempt " + attempt);
                return;
            } catch (RuntimeException exception) {
                last = exception;
                tracker.log(id, compensation.stepName(), "COMPENSATE", "RETRY", "Attempt " + attempt + ": " + reason(exception));
            }
        }
        throw last;
    }
    private HttpStatus status(Exception exception) {
        if (exception instanceof FeignException.NotFound) return HttpStatus.BAD_REQUEST;
        if (exception instanceof FeignException || exception.getCause() instanceof java.net.ConnectException) return HttpStatus.SERVICE_UNAVAILABLE;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    private String reason(Throwable exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
    }
}
