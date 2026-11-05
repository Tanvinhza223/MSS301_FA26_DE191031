package com.mss301.watch.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SagaLogs")
public class SagaLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "LogID") private Long logId;
    @Column(name = "SagaID", nullable = false, length = 36) private String sagaId;
    @Column(name = "SequenceNo", nullable = false) private Integer sequence;
    @Column(name = "StepName", nullable = false, length = 50) private String stepName;
    @Column(name = "Action", nullable = false, length = 30) private String action;
    @Column(name = "Status", nullable = false, length = 20) private String status;
    @Column(name = "Details", columnDefinition = "NVARCHAR(MAX)") private String details;
    @Column(name = "CreatedAt", nullable = false) private LocalDateTime createdAt;
    protected SagaLog() {}
    public SagaLog(String sagaId, Integer sequence, String stepName, String action, String status, String details) {
        this.sagaId = sagaId; this.sequence = sequence; this.stepName = stepName; this.action = action;
        this.status = status; this.details = details; this.createdAt = LocalDateTime.now();
    }
    public Long getLogId() { return logId; }
    public String getSagaId() { return sagaId; }
    public Integer getSequence() { return sequence; }
    public String getStepName() { return stepName; }
    public String getAction() { return action; }
    public String getStatus() { return status; }
    public String getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

