package com.example.s_balneare.domain.beach;

import com.example.s_balneare.domain.common.Address;

public class BeachGeneral {
    private String beachName;
    private String beachDescription;
    private Address beachAddress;
    private String telephoneNumber;
    private Parking parkingSpace;

    public BeachGeneral(String beachName, String beachDescription, Address beachAddress, String telephoneNumber, Parking parkingSpace) {
        this.beachName = beachName;
        this.beachDescription = beachDescription;
        this.beachAddress = beachAddress;
        this.telephoneNumber = telephoneNumber;
        this.parkingSpace = parkingSpace;
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

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public Parking getParkingSpace() {
        return parkingSpace;
    }
}