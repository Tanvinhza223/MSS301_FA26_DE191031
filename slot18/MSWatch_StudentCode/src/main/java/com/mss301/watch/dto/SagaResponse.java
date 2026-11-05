package com.mss301.watch.dto;
import com.mss301.watch.saga.SagaStatus;
public record SagaResponse<T>(String sagaId, SagaStatus sagaStatus, T result) {}

