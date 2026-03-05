package com.example.s_balneare.domain.beach;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record Season(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Integer pricingsId,
        List<Integer> zonePricingIds
) {

    //costruttore compatto per assicurarsi l'integrità dei valori
    public Season {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("ERROR: name cannot be blank");
        if (name.length() > 50) throw new IllegalArgumentException("ERROR: name cannot exceed 50 characters");

        if (startDate == null || endDate == null) throw new IllegalArgumentException("ERROR: dates cannot be null");
        if (!startDate.isBefore(endDate)) throw new IllegalArgumentException("ERROR: endDate must be strictly after startDate");

        if (pricingsId == null || pricingsId <= 0) throw new IllegalArgumentException("ERROR: pricingsId not valid");

        if (zonePricingIds == null || zonePricingIds.isEmpty()) throw new IllegalArgumentException("ERROR: at least one zone must be set for season");
        for (Integer zId : zonePricingIds) {
            if (zId == null || zId <= 0) throw new IllegalArgumentException("ERROR: at least one zonePricingId is not valid");
        }
    }

    //metodi Business
    public boolean includes(LocalDate date) {
        if (date == null) return false;
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
                (date.isBefore(endDate) || date.isEqual(endDate));
    }

    //metodi wither
    public Season withName(String name) {
        return new Season(name, startDate, endDate, pricingsId, zonePricingIds);
    }
    public Season withDates(LocalDate startDate, LocalDate endDate) {
        return new Season(name, startDate, endDate, pricingsId, zonePricingIds);
    }
    public Season withPricingsId(Integer pricingsId) {
        return new Season(name, startDate, endDate, pricingsId, zonePricingIds);
    }
    public Season withZonePricingIds(List<Integer> zonePricingIds) {
        return new Season(name, startDate, endDate, pricingsId, zonePricingIds);
    }

    //pattern Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer pricingsId;
        private List<Integer> zonePricingIds = new ArrayList<>();

        public Builder() {}

        //costruttore copia
        public Builder(Season original) {
            this.name = original.name();
            this.startDate = original.startDate();
            this.endDate = original.endDate();
            this.pricingsId = original.pricingsId();
            this.zonePricingIds = new ArrayList<>(original.zonePricingIds());
        }

        public Builder name(String val) { name = val; return this; }
        public Builder startDate(LocalDate val) { startDate = val; return this; }
        public Builder endDate(LocalDate val) { endDate = val; return this; }
        public Builder pricingsId(Integer val) { pricingsId = val; return this; }
        public Builder zonePricingIds(List<Integer> val) {
            if (val != null) this.zonePricingIds = new ArrayList<>(val);
            return this;
        }

        public Season build() {
            return new Season(name, startDate, endDate, pricingsId, zonePricingIds);
        }
    }
}