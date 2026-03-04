package com.example.s_balneare.domain.moderation;

import com.example.s_balneare.domain.user.Role;

import java.time.Instant;

//TODO: da implementarla nel pattern DDD-lite

public class Report {
    private final Integer id;
    private final Integer reporterId;
    private final Integer reportedId;
    private final ReportTargetType reportedType;

    private final String description;
    private final Instant createdAt;
    private ReportStatus status;

    public Report(Integer id, Integer reporterId, Integer reportedId, Role reportedRole, String description) {
        if (reporterId == null || reportedId == null || reporterId <= 0 || reportedId <= 0) throw new IllegalArgumentException("ERROR: reporter and/or reported users are not initialized correctly");
        if (reporterId.equals(reportedId)) throw new IllegalArgumentException("ERROR: reporter and reported cannot be the same user");
        if (description.isBlank()) throw new IllegalArgumentException("ERROR: description cannot be empty");

        this.id = id;
        this.reporterId = reporterId;
        this.reportedId = reportedId;

        if(reportedRole == Role.CUSTOMER) {
            reportedType = ReportTargetType.USER;
        } else {
            reportedType = ReportTargetType.BEACH;
        }

        this.description = description;
        this.createdAt = Instant.now();
        this.status = ReportStatus.PENDING;
    }

    public Integer getId() {
        return id;
    }

    public Integer getReporterId() {
        return reporterId;
    }

    public Integer getReportedId() {
        return reportedId;
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