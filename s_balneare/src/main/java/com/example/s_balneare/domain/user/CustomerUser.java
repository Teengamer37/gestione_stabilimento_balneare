package com.example.s_balneare.domain.user;

//TODO: da implementarla nel pattern DDD-lite, guardare i costruttori

public final class CustomerUser extends AppUser {
    private String phoneNumber;
    private int addressId;
    private boolean active;


    public CustomerUser(int id, String email, String username, String name, String surname, boolean active, String phoneNumber, int addressId) {
        super(id, email, username, name, surname);
        this.phoneNumber = phoneNumber;
        this.addressId = addressId;
        this.active = active;
    }

    public CustomerUser(Integer id){super(id);}

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

    public int getAddressId() {return addressId;}

    public boolean isActive() {return active;}

    public void setActive(boolean active) {this.active = active;}
}