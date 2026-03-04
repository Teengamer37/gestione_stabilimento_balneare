package com.example.s_balneare.domain.layout;

import java.util.List;

//TODO: da implementarla nel pattern DDD-lite

public class Zone {
    private final Integer id;
    private String name;
    private List<Integer> spotIds;

    public Zone(Integer id, String name, List<Integer> spotIds) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("ERROR: beachName cannot be empty");

        this.id = id;
        this.name = name;
        this.spotIds = spotIds;
    }

    public Integer getId() {
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