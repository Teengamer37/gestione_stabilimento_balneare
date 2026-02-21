package com.example.s_balneare.domain.moderation;

import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.Role;

import java.time.Instant;

public class Report {
    private final int id;
    private final AppUser reporter;
    private final AppUser reported;
    private final ReportTargetType reportedType;

    private final String description;
    private final Instant createdAt;
    private ReportStatus status;

    public Report(int id, AppUser reporter, AppUser reported, String description) {
        if (reporter == null || reported == null) throw new IllegalArgumentException("ERROR: reporter and/or reported users cannot be null");
        if (reporter.getId().equals(reported.getId())) throw new IllegalArgumentException("ERROR: reporter and reported cannot be the same user");
        if (description.isBlank()) throw new IllegalArgumentException("ERROR: description cannot be empty");

        this.id = id;
        this.reporter = reporter;
        this.reported = reported;

        if(reported.getRole() == Role.CUSTOMER) {
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

    public AppUser getReporter() {
        return reporter;
    }

    public AppUser getReported() {
        return reported;
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