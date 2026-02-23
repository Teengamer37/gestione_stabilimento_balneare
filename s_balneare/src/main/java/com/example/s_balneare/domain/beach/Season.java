package com.example.s_balneare.domain.beach;

import java.time.LocalDate;
import java.util.List;

public class Season {
    private final int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Pricing pricing;
    private final List<ZonePricing> zonePricings;

    public Season(int id, LocalDate startDate, LocalDate endDate, Pricing pricing, List<ZonePricing> zonePricings) {
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("ERROR: end date must be after start date");
        if (zonePricings.isEmpty()) throw new IllegalArgumentException("ERROR: at least one zone must be set for season");

        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricing = pricing;
        this.zonePricings = zonePricings;
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

    public Pricing getPricing() {
        return pricing;
    }

    public List<ZonePricing> getZonePricings() {
        return zonePricings;
    }

    public boolean includes(LocalDate date) {
        return ((date.isEqual(startDate) || date.isAfter(startDate)) && (date.isBefore(endDate) || date.isEqual(endDate)));
    }
}