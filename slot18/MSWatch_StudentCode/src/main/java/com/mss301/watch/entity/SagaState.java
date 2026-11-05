package com.mss301.watch.entity;

import com.mss301.watch.saga.SagaStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SagaStates")
public class SagaState {
    @Id @Column(name = "SagaID", length = 36) private String sagaId;
    @Column(name = "SagaType", nullable = false, length = 30) private String sagaType;
    @Enumerated(EnumType.STRING) @Column(name = "Status", nullable = false, length = 20) private SagaStatus status;
    @Column(name = "CurrentStep", length = 50) private String currentStep;
    @Column(name = "FailedStep", length = 50) private String failedStep;
    @Column(name = "FailureReason", columnDefinition = "NVARCHAR(MAX)") private String failureReason;
    @Column(name = "CreatedAt", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UpdatedAt", nullable = false) private LocalDateTime updatedAt;
    protected SagaState() {}
    public SagaState(String sagaId, String sagaType) {
        this.sagaId = sagaId; this.sagaType = sagaType; this.status = SagaStatus.PENDING;
        this.createdAt = LocalDateTime.now(); this.updatedAt = createdAt;
    }
    public void transition(SagaStatus status, String currentStep, String failedStep, String reason) {
        this.status = status; this.currentStep = currentStep; this.failedStep = failedStep;
        this.failureReason = reason; this.updatedAt = LocalDateTime.now();
    }
    public String getSagaId() { return sagaId; }
    public String getSagaType() { return sagaType; }
    public SagaStatus getStatus() { return status; }
    public String getCurrentStep() { return currentStep; }
    public String getFailedStep() { return failedStep; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

