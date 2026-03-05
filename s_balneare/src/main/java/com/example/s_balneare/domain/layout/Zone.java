package com.example.s_balneare.domain.layout;

import java.util.ArrayList;
import java.util.List;

public record Zone(
        String name,
        List<Spot> spots
) {
    public Zone {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("ERROR: Zone name cannot be blank");
        if (name.length() > 50) throw new IllegalArgumentException("ERROR: Zone name cannot exceed 50 characters");

        //protegge la lista da modifiche esterne
        spots = (spots == null) ? List.of() : List.copyOf(spots);
    }

    //Static Factory per una Zone nuova
    public static Zone create(String name) {
        return new Zone(name, new ArrayList<>());
    }

    //metodi wither
    public Zone withName(String newName) {
        return new Zone(newName, spots);
    }
    public Zone withSpots(List<Spot> newSpots) {
        return new Zone(name, newSpots);
    }

    //pattern Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private List<Spot> spots = new ArrayList<>();

        public Builder() {}

        //costruttore copia
        public Builder(Zone original) {
            this.name = original.name();
            this.spots = new ArrayList<>(original.spots());
        }

        public Builder name(String val) { name = val; return this; }
        public Builder spots(List<Spot> val) {
            if (val != null) this.spots = new ArrayList<>(val);
            return this;
        }

        public Zone build() {
            return new Zone(name, spots);
        }
    }
}