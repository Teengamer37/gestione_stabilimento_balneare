package com.example.s_balneare.domain.user;

import com.example.s_balneare.domain.common.Address;

import java.util.UUID;

public final class CustomerUser extends AppUser {
    private String phoneNumber;
    private int addressId;


    public CustomerUser(int id, String email, String username, String name, String surname, boolean active, String phoneNumber, int addressId) {
        super(id, email, username, name, surname, active);
        this.phoneNumber = phoneNumber;
        this.addressId = addressId;
    }

    @Override
    public Role getRole() {
        return Role.CUSTOMER;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAddress() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
}