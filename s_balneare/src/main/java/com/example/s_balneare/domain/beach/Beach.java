package com.example.s_balneare.domain.beach;

import com.example.s_balneare.domain.common.Address;

import java.util.UUID;

public class Beach {
    private final UUID id;
    private String beachName;
    private String beachDescription;
    private Address beachAddress;
    private String telephoneNumber;
    private boolean active;

    public Beach(UUID id, String beachName, String beachDescription, Address beachAddress, String telephoneNumber, boolean active) {
        this.id = id;
        this.beachName = beachName;
        this.beachDescription = beachDescription;
        this.beachAddress = beachAddress;
        this.telephoneNumber = telephoneNumber;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public String getBeachName() {
        return beachName;
    }

    public void setBeachName(String beachName) {
        this.beachName = beachName;
    }

    public String getBeachDescription() {
        return beachDescription;
    }

    public void setBeachDescription(String beachDescription) {
        this.beachDescription = beachDescription;
    }

    public Address getBeachAddress() {
        return beachAddress;
    }

    public void setBeachAddress(Address beachAddress) {
        this.beachAddress = beachAddress;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
