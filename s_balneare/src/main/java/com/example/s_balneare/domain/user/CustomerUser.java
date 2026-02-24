package com.example.s_balneare.domain.user;

import com.example.s_balneare.domain.common.Address;

import java.util.UUID;

public final class CustomerUser extends AppUser {
    private String phoneNumber;
    private Address address;


    public CustomerUser(String name, String surname, String phoneNumber, Address address, int id, String email, String username, boolean active) {
        super(id, email, username,name, surname, active);
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    @Override
    public Role getRole() {
        return Role.CUSTOMER;
    }
}