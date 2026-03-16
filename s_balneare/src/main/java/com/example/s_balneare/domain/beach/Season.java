package com.example.s_balneare.domain.beach;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/// Definisce una stagione specifica per una spiaggia
public record Season(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Pricing pricing,
        List<ZoneTariff> zoneTariffs
) {
    //costruttore compatto per assicurarsi l'integrità dei valori
    public Season {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("ERROR: name cannot be blank");
        if (name.length() > 50) throw new IllegalArgumentException("ERROR: name cannot exceed 50 characters");

        if (startDate == null || endDate == null) throw new IllegalArgumentException("ERROR: dates cannot be null");
        if (!startDate.isBefore(endDate))
            throw new IllegalArgumentException("ERROR: endDate must be strictly after startDate");

        if (pricing == null) throw new IllegalArgumentException("ERROR: pricing cannot be null");

        if (zoneTariffs == null || zoneTariffs.isEmpty())
            throw new IllegalArgumentException("ERROR: at least one zoneTariff must be set for season");
        for (ZoneTariff zoneTariff : zoneTariffs) {
            if (zoneTariff == null) throw new IllegalArgumentException("ERROR: at least one zoneTariff is not valid");
        }
    }

    //metodo per usare il pattern Builder per creare/manipolare Season
    public static Builder builder() {
        return new Builder();
    }

    //metodi Business
    public boolean includes(LocalDate date) {
        if (date == null) return false;
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
                (date.isBefore(endDate) || date.isEqual(endDate));
    }

    //metodi wither
    public Season withName(String name) {
        return new Season(name, startDate, endDate, pricing, zoneTariffs);
    }
    public Season withDates(LocalDate startDate, LocalDate endDate) {
        return new Season(name, startDate, endDate, pricing, zoneTariffs);
    }
    public Season withPricing(Pricing pricing) {
        return new Season(name, startDate, endDate, pricing, zoneTariffs);
    }
    public Season withZoneTariffs(List<ZoneTariff> zoneTariffs) {
        return new Season(name, startDate, endDate, pricing, zoneTariffs);
    }

    //pattern Builder
    public static class Builder {
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private Pricing pricing;
        private List<ZoneTariff> zoneTariffs = new ArrayList<>();

        public Builder() {
        }

        //costruttore copia
        public Builder(Season original) {
            this.name = original.name();
            this.startDate = original.startDate();
            this.endDate = original.endDate();
            this.pricing = original.pricing();
            this.zoneTariffs = new ArrayList<>(original.zoneTariffs());
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder startDate(LocalDate val) {
            startDate = val;
            return this;
        }

        public Builder endDate(LocalDate val) {
            endDate = val;
            return this;
        }

        public Builder pricing(Pricing val) {
            pricing = val;
            return this;
        }

        public Builder zoneTariffs(List<ZoneTariff> val) {
            if (val != null) this.zoneTariffs = new ArrayList<>(val);
            return this;
        }

        public Season build() {
            return new Season(name, startDate, endDate, pricing, zoneTariffs);
        }
    }
}