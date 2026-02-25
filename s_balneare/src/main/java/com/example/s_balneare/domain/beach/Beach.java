package com.example.s_balneare.domain.beach;

import java.util.List;

public class Beach {
    private final int id;
    private final int ownerId;

    private int beachGeneral;
    private int beachInventory;
    private int beachServices;

    private String extraInfo;
    private List<Integer> seasons;
    private boolean active;

    public Beach(int id, int ownerId, int beachGeneral, int beachInventory, int beachServices, String extraInfo, List<Integer> seasons, boolean active) {
        this.id = id;
        this.ownerId = ownerId;
        this.beachGeneral = beachGeneral;
        this.beachInventory = beachInventory;
        this.beachServices = beachServices;
        this.extraInfo = extraInfo;
        this.seasons = seasons;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getBeachGeneral() {
        return beachGeneral;
    }

    public void setBeachGeneral(int beachGeneral) {
        this.beachGeneral = beachGeneral;
    }

    public int getBeachInventory() {
        return beachInventory;
    }

    public void setBeachInventory(int beachInventory) {
        this.beachInventory = beachInventory;
    }

    public int getBeachServices() {
        return beachServices;
    }

    public void setBeachServices(int beachServices) {
        this.beachServices = beachServices;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public List<Integer> getSeasons() {
        return seasons;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}