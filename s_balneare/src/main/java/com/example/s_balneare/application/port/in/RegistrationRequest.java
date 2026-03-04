package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.user.Role;

public class RegistrationRequest {
    //attributi comuni a tutti gli utenti
    private final int id;
    private final Role type;
    private final String email;
    private final String username;
    private final String name;
    private final String surname;

    //attributi esclusivi del customer
    private final String phoneNumber;
    private final int addressId;
    private final boolean active;

    //costruttore privato: l'oggetto può essere creato solo tramite il Builder
    private RegistrationRequest(Builder builder) {
        this.type = builder.type;
        this.email = builder.email;
        this.username = builder.username;
        this.name = builder.name;
        this.surname = builder.surname;
        this.phoneNumber = builder.phoneNumber;
        this.addressId = builder.addressId;
        this.active = builder.active;
        this.id = builder.id;
    }

    //getters (sola lettura per garantire l'immutabilità)
    public Role getType() { return type; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getAddressId() { return addressId; }
    public boolean isActive(){return active;}
    public int getId() { return id; }

    // --- INNER CLASS BUILDER ---
    public static class Builder {
        //campi del Builder (stessi della classe esterna)
        private Integer id;
        private Role type;
        private String email;
        private String username;
        private String name;
        private String surname;
        private String phoneNumber;
        private Integer addressId ;
        private boolean active;// Default a 0

        //il costruttore del Builder richiede i parametri minimi obbligatori per OGNI utente
        public Builder(Role type, String email, String username) {
            this.type = type;
            this.email = email;
            this.username = username;
        }

        //utilizzato quando si preleva dati dal DB per settare l'ID, se altrimenti la richiesta è nuova l'ID è nullo
        public Builder DatabaseRequest(int id){
            this.id = id;
            return this;
        }

        //metodi wither
        public Builder withName(String name, String surname) {
            this.name = name;
            this.surname = surname;
            return this;
        }
        //utilizzato solo per i customer
        public Builder withCustomerData(String phoneNumber, int addressId, boolean active) {
            this.phoneNumber = phoneNumber;
            this.addressId = addressId;
            this.active= active;
            return this;
        }

        //valida e restituisce la Request
        public RegistrationRequest build() {
            registrationRequestLegal();
            customerRegistrationRequestLegal();
            return new RegistrationRequest(this);
        }

        public void customerRegistrationRequestLegal(){
            if (type == Role.CUSTOMER) {
                if (phoneNumber == null || addressId == null)
                    throw new IllegalArgumentException("ERROR: required fields are mandatory");
            }
        }

        public void registrationRequestLegal(){
            if (type == null || name == null || surname == null || username == null || email == null) {
                throw new IllegalArgumentException("ERROR: required fields are mandatory");
            }
        }
    }
}