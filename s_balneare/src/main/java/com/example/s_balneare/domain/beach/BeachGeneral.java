package com.example.s_balneare.domain.beach;

public class BeachGeneral {
    private String name;
    private String description;
    private int addressId;
    private String telephoneNumber;
    private int parkingSpaceId;

    public BeachGeneral(String name, String description, int addressId, String telephoneNumber, int parkingSpaceId) {
        if (addressId <= 0) throw new IllegalArgumentException("ERROR: addressId not valid");
        if (parkingSpaceId <= 0) throw new IllegalArgumentException("ERROR: parkingSpaceId not valid");

        this.name = name;
        this.description = description;
        this.addressId = addressId;
        this.telephoneNumber = telephoneNumber;
        this.parkingSpaceId = parkingSpaceId;
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

    public int getAddressId() {
        return addressId;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public int getParkingSpaceId() {
        return parkingSpaceId;
    }
}