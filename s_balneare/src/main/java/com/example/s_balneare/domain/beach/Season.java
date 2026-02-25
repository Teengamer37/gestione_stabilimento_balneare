package com.example.s_balneare.domain.beach;

import java.time.LocalDate;
import java.util.List;

public class Season {
    private final int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private int pricingId;
    private final List<Integer> zonePricingIds;

    public Season(int id, LocalDate startDate, LocalDate endDate, int pricingId, List<Integer> zonePricingIds) {
        if (pricingId <= 0) throw new IllegalArgumentException("ERROR: pricingId not valid");
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("ERROR: end date must be after start date");
        if (zonePricingIds.isEmpty()) throw new IllegalArgumentException("ERROR: at least one zone must be set for season");

        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricingId = pricingId;
        this.zonePricingIds = zonePricingIds;
    }

    public int getId() {
        return id;
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

    public int getPricingId() {
        return pricingId;
    }

    public List<Integer> getZonePricingIds() {
        return zonePricingIds;
    }

    public boolean includes(LocalDate date) {
        return ((date.isEqual(startDate) || date.isAfter(startDate)) && (date.isBefore(endDate) || date.isEqual(endDate)));
    }
}