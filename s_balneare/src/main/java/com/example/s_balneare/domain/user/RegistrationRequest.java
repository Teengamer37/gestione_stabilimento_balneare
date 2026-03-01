package com.example.s_balneare.domain.user;

public class RegistrationRequest {
    //dati comuni a tutti gli utenti
    private Role type;
    private String email;
    private String username;
    private String name;
    private String surname;
    //dati esclusivi del customer
    private String phoneNumber;
    private int addressI;
    //dati esclusivi dell'owner
    private int beachId;

    public void setBeachId(int beachId) {this.beachId = beachId;}

    public int getAddressI() {return addressI;}

    public void setAddressI(int addressI) {this.addressI = addressI;}

    public void setType(Role type) {this.type = type;}

    public Role getType() {return type;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}

    public void setSurname(String surname) {this.surname = surname;}

    public String getPhoneNumber() {return phoneNumber;}

    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

    public Integer getBeachId() {return beachId;}

    public void setBeachId(Integer beachId) {this.beachId = beachId;}
}
