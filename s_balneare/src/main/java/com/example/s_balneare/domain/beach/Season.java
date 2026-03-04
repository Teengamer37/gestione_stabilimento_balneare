package com.example.s_balneare.domain.beach;

import java.time.LocalDate;
import java.util.List;

//TODO: da implementarla nel pattern DDD-lite

public class Season {
    private final Integer id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pricingId;
    private final List<Integer> zonePricingIds;

    public Season(Integer id, LocalDate startDate, LocalDate endDate, Integer pricingId, List<Integer> zonePricingIds) {
        if (pricingId == null || pricingId <= 0) throw new IllegalArgumentException("ERROR: pricingId not valid");
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("ERROR: end date must be after start date");
        if (zonePricingIds.isEmpty()) throw new IllegalArgumentException("ERROR: at least one zone must be set for season");

        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricingId = pricingId;
        this.zonePricingIds = zonePricingIds;
    }

    public Integer getId() {
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

    public Integer getPricingId() {
        return pricingId;
    }

    public List<Integer> getZonePricingIds() {
        return zonePricingIds;
    }

    public boolean includes(LocalDate date) {
        return ((date.isEqual(startDate) || date.isAfter(startDate)) && (date.isBefore(endDate) || date.isEqual(endDate)));
    }
}