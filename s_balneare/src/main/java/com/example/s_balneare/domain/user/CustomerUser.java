package com.example.s_balneare.domain.user;

import com.example.s_balneare.domain.common.Address;

import java.util.UUID;

public final class CustomerUser extends AppUser {
    private String phoneNumber;
    private Address address;


    public CustomerUser(int id, String email, String username, String name, String surname, boolean active, String phoneNumber, Address address) {
        super(id, email, username, name, surname, active);
        this.phoneNumber = phoneNumber;
        this.address = address;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}