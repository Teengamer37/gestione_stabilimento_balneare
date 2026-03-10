package com.example.s_balneare.application.port.in.user;

public class CustomerUserRequest extends AppUserRequest{

    private final String phoneNumber;
    private final String street;
    private final String streetNumber;
    private final String city;
    private final String zipCode;
    private final String country;

    public CustomerUserRequest(String email, String username, String name, String surname, String phoneNumber, boolean active, String phoneNumber1, String street, String streetNumber, String city, String zipCode, String country) {
        super(email, username, name, surname, phoneNumber, active);
        this.phoneNumber = phoneNumber1;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getPhoneNumber() {return phoneNumber;}

    public String getStreet() {return street;}

    public String getCity() {return city;}

    public String getStreetNumber() {return streetNumber;}

    public String getZipCode() {return zipCode;}

    public String getCountry() {return country;}
}
