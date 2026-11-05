package com.mss301.watch.dto;
import com.mss301.watch.entity.SagaLog;
import com.mss301.watch.entity.SagaState;
import java.util.List;
public record SagaDetails(SagaState saga, List<SagaLog> logs) {}

