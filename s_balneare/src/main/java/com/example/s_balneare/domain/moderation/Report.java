package com.example.s_balneare.domain.moderation;

import java.time.Instant;
import java.util.UUID;

public class Report {
    private final UUID id;
    private final UUID reporterID;
    private final UUID reportedID;
    private final ReportTargetType reportedType;

    private final String description;
    private final Instant createdAt;
    private ReportStatus status;

    public Report(UUID id, UUID reporterID, UUID reportedID, ReportTargetType reportedType, String description) {
        this.id = id;
        this.reporterID = reporterID;
        this.reportedID = reportedID;
        this.reportedType = reportedType;
        this.description = description;
        this.createdAt = Instant.now();
        this.status = ReportStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReporterID() {
        return reporterID;
    }

    public UUID getReportedID() {
        return reportedID;
    }

    public ReportTargetType getReportedType() {
        return reportedType;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
}
