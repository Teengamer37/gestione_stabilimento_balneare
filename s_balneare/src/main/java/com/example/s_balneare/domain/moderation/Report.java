package com.example.s_balneare.domain.moderation;

import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.Role;

import java.time.Instant;

public class Report {
    private final int id;
    private final int reporterId;
    private final int reportedId;
    private final ReportTargetType reportedType;

    private final String description;
    private final Instant createdAt;
    private ReportStatus status;

    public Report(int id, int reporterId, int reportedId, Role reportedRole, String description) {
        if (reporterId <= 0 || reportedId <= 0) throw new IllegalArgumentException("ERROR: reporter and/or reported users are not initialized correctly");
        if (reporterId == reportedId) throw new IllegalArgumentException("ERROR: reporter and reported cannot be the same user");
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

    public int getId() {
        return id;
    }

    public int getReporterId() {
        return reporterId;
    }

    public int getReportedId() {
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