package com.example.s_balneare.domain.beach;

import java.util.List;

public class Beach {
    private final int id;
    private final int ownerId;

    private int beachGeneralId;
    private int beachInventoryId;
    private int beachServicesId;

    private String extraInfo;
    private List<Integer> seasonIds;
    private boolean active;

    public Beach(int id, int ownerId, int beachGeneralId, int beachInventoryId, int beachServicesId, String extraInfo, List<Integer> seasonIds, boolean active) {
        if (ownerId <= 0) throw new IllegalArgumentException("ERROR: ownerId not valid");
        if (beachGeneralId <= 0) throw new IllegalArgumentException("ERROR: beachGeneralId not valid");
        if (beachInventoryId <= 0) throw new IllegalArgumentException("ERROR: beachInventoryId not valid");
        if (beachServicesId <= 0) throw new IllegalArgumentException("ERROR: beachServicesId not valid");
        if (seasonIds == null || seasonIds.isEmpty()) throw new IllegalArgumentException("ERROR: at least one season must be set for beach");

        this.id = id;
        this.ownerId = ownerId;
        this.beachGeneralId = beachGeneralId;
        this.beachInventoryId = beachInventoryId;
        this.beachServicesId = beachServicesId;
        this.extraInfo = extraInfo;
        this.seasonIds = seasonIds;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getBeachGeneralId() {
        return beachGeneralId;
    }

    public int getBeachInventoryId() {
        return beachInventoryId;
    }

    public int getBeachServicesId() {
        return beachServicesId;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public List<Integer> getSeasonIds() {
        return seasonIds;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}