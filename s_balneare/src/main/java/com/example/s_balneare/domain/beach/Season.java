package com.example.s_balneare.domain.beach;

import java.time.LocalDate;
import java.util.UUID;

public class Season {
    private final UUID id;
    private final UUID beachID;
    private LocalDate startDate;
    private LocalDate endDate;

    public Season(UUID id, UUID beachID, LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("ERROR: end date must be after start date");
        }

        this.id = id;
        this.beachID = beachID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBeachID() {
        return beachID;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean includes(LocalDate date) {
        return ((date.isEqual(startDate) || date.isAfter(startDate)) && (date.isBefore(endDate) || date.isEqual(endDate)));
    }
}
