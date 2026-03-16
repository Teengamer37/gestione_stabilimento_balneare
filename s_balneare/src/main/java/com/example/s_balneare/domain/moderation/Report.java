package com.example.s_balneare.domain.moderation;

import java.time.Instant;

/// Rappresenta una segnalazione (report) inviata da un utente
public class Report {
    //dati attori report
    private final Integer id;
    private final Integer reporterId;
    private final Integer reportedId;
    private final ReportTargetType reportedType;

    //dati report
    private final String description;
    private final Instant createdAt;
    private final Integer bookingId;
    private ReportStatus status;

    //costruttore completo per assicurarsi l'integrità dei valori
    public Report(Integer id, Integer reporterId, Integer reportedId, ReportTargetType reportedType, String description, Instant createdAt, ReportStatus status, Integer bookingId) {
        checkReporterReported(reporterId, reportedId);
        checkDescription(description);
        checkCreatedAt(createdAt);
        checkBookingId(bookingId);
        this.id = id;
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.reportedType = reportedType;
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
        this.bookingId = bookingId;
    }

    //getters
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
    public Integer getBookingId() {
        return bookingId;
    }

    //---- METODI BUSINESS ----

    /**
     * Cambia stato di report in APPROVED.
     *
     * @throws IllegalStateException se lo stato corrente non è PENDING
     */
    public void approve() {
        if (this.status != ReportStatus.PENDING) {
            throw new IllegalStateException("ERROR: report cannot be approved, it is in: " + this.status);
        }
        this.status = ReportStatus.APPROVED;
    }

    /**
     * Cambia stato di report in REJECTED.
     *
     * @throws IllegalStateException se lo stato corrente non è PENDING
     */
    public void reject() {
        if (this.status != ReportStatus.PENDING) {
            throw new IllegalStateException("ERROR: report cannot be rejected, it is in: " + this.status);
        }
        this.status = ReportStatus.REJECTED;
    }

    //---- METODI CHECKERS ----
    private void checkReporterReported(Integer reporterId, Integer reportedId) {
        if (reporterId == null || reportedId == null || reporterId <= 0 || reportedId <= 0)
            throw new IllegalArgumentException("ERROR: reporter and/or reported users are not initialized correctly");
        if (reporterId.equals(reportedId))
            throw new IllegalArgumentException("ERROR: reporter and reported cannot be the same user");
    }

    private void checkDescription(String description) {
        if (description.isBlank()) throw new IllegalArgumentException("ERROR: description cannot be empty");
        if (description.length() > 1024)
            throw new IllegalArgumentException("ERROR: description cannot be longer than 1024 characters");
    }

    private void checkCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new IllegalArgumentException("ERROR: createdAt must not be null");
        if (createdAt.isAfter(Instant.now())) throw new IllegalArgumentException("ERROR: createdAt cannot be in the future");
    }

    private void checkBookingId(Integer bookingId) {
        if (bookingId == null) throw new IllegalArgumentException("ERROR: no booking selected");
    }
}