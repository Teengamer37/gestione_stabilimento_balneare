package com.example.s_balneare.domain.beach;

import java.time.LocalDate;

public class Season {
    private final int id;
    private final Beach beach;
    private LocalDate startDate;
    private LocalDate endDate;
    private Pricing pricing;

    public Season(int id, Beach beach, LocalDate startDate, LocalDate endDate, Pricing pricing) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("ERROR: end date must be after start date");
        }

        this.id = id;
        this.beach = beach;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricing = pricing;
    }

    public int getId() {
        return id;
    }

    public Beach getBeach() {
        return beach;
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

    public boolean includes(LocalDate date) {
        return ((date.isEqual(startDate) || date.isAfter(startDate)) && (date.isBefore(endDate) || date.isEqual(endDate)));
    }
}