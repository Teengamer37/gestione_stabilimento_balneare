package com.example.s_balneare.domain.beach;

import com.example.s_balneare.domain.user.OwnerUser;

import java.util.List;

public class Beach {
    private final int id;
    private final OwnerUser owner;

    private BeachGeneral beachGeneral;
    private BeachInventory beachInventory;
    private BeachServices beachServices;

    private String extraInfo;
    private List<Season> seasons;
    private boolean active;

    public Beach(int id, OwnerUser owner, BeachGeneral beachGeneral, BeachInventory beachInventory, BeachServices beachServices, String extraInfo, List<Season> seasons, boolean active) {
        this.id = id;
        this.owner = owner;
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

    public OwnerUser getOwner() {
        return owner;
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

    public List<Season> getSeasons() {
        return seasons;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}