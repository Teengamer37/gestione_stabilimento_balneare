package com.example.s_balneare.domain.beach;

import java.util.UUID;

public class Beach {
    private final UUID id;
    private final UUID ownerID;

    private BeachGeneral beachGeneral;
    private BeachInventory beachInventory;
    private BeachServices beachServices;

    private String extraInfo;
    private boolean active;

    public Beach(UUID id, UUID ownerID, BeachGeneral beachGeneral, BeachInventory beachInventory, BeachServices beachServices, String extraInfo, boolean active) {
        this.id = id;
        this.ownerID = ownerID;
        this.beachGeneral = beachGeneral;
        this.beachInventory = beachInventory;
        this.beachServices = beachServices;
        this.extraInfo = extraInfo;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    public BeachGeneral getBeachGeneral() {
        return beachGeneral;
    }

    public void setBeachGeneral(BeachGeneral beachGeneral) {
        this.beachGeneral = beachGeneral;
    }

    public BeachInventory getBeachInventory() {
        return beachInventory;
    }

    public void setBeachInventory(BeachInventory beachInventory) {
        this.beachInventory = beachInventory;
    }

    public BeachServices getBeachServices() {
        return beachServices;
    }

    public void setBeachServices(BeachServices beachServices) {
        this.beachServices = beachServices;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
