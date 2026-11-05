package com.mss301.watch.exception;

import com.mss301.watch.saga.SagaStatus;
import org.springframework.http.HttpStatus;

public class SagaExecutionException extends RuntimeException {
    private final String sagaId;
    private final SagaStatus sagaStatus;
    private final String failedStep;
    private final HttpStatus httpStatus;
    public SagaExecutionException(String message, String sagaId, SagaStatus sagaStatus, String failedStep, HttpStatus httpStatus, Throwable cause) {
        super(message, cause); this.sagaId = sagaId; this.sagaStatus = sagaStatus; this.failedStep = failedStep; this.httpStatus = httpStatus;
    }
    public String getSagaId() { return sagaId; }
    public SagaStatus getSagaStatus() { return sagaStatus; }
    public String getFailedStep() { return failedStep; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}

