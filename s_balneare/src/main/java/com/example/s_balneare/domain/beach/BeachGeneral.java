package com.example.s_balneare.domain.beach;

import com.example.s_balneare.domain.common.Address;

public class BeachGeneral {
    private String name;
    private String description;
    private Address address;
    private String telephoneNumber;
    private Parking parkingSpace;

    public BeachGeneral(String name, String description, Address address, String telephoneNumber, Parking parkingSpace) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.parkingSpace = parkingSpace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Address getAddress() {
        return address;
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