package com.example.s_balneare.domain.layout;

import java.util.List;

public class Zone {
    private final int id;
    private String name;
    private List<Integer> spotIds;

    public Zone(int id, String name, List<Integer> spotIds) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("ERROR: beachName cannot be empty");

        this.id = id;
        this.name = name;
        this.spotIds = spotIds;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getSpotIds() {
        return spotIds;
    }
}