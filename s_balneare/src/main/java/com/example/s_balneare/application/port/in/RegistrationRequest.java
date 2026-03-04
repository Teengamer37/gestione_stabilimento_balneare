package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.user.Role;

public class RegistrationRequest {
    // 1. Attributi comuni a tutti gli utenti
    private final int id;
    private final Role type;
    private final String email;
    private final String username;
    private final String name;
    private final String surname;

    // 2. Attributi esclusivi del customer
    private final String phoneNumber;
    private final int addressId;
    private final boolean active;

    // Costruttore privato: l'oggetto può essere creato solo tramite il Builder
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

    // Getter (Sola lettura per garantire l'immutabilità)
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
        // Campi del builder (stessi della classe esterna)
        private Integer id;
        private Role type;
        private String email;
        private String username;
        private String name;
        private String surname;
        private String phoneNumber;
        private Integer addressId ;
        private boolean active;// Default a 0

        // Il costruttore del Builder richiede i parametri minimi obbligatori per OGNI utente
        public Builder(Role type, String email, String username) {
            this.type = type;
            this.email = email;
            this.username = username;
        }

        //Utilizzato quando si preleva dati dal db per settare l'id, se altrimenti la richiesta è nuova l'id è nullo
        public Builder DataBaseRequest(int id){
            this.id = id;
            return this;
        }

        public Builder withName(String name, String surname) {
            this.name = name;
            this.surname = surname;
            return this;
        }
        // Utilizzato solo per i customer
        public Builder withCustomerData(String phoneNumber, int addressId, boolean active) {
            this.phoneNumber = phoneNumber;
            this.addressId = addressId;
            this.active= active;
            return this;
        }

        // Metodo build: Valida e restituisce la Request
        public RegistrationRequest build() {
            RegistrationRequestLegal();
            CustomerRegistrationRequestLegal();
            return new RegistrationRequest(this);
        }

        public void CustomerRegistrationRequestLegal(){
            if (type == Role.CUSTOMER) {
                if (phoneNumber == null || addressId == null)
                    throw new IllegalArgumentException("Required fields are mandatory");
            }
        }

        public void RegistrationRequestLegal(){
            if (type == null || name == null || surname == null || username == null || email == null) {
                throw new IllegalArgumentException("Required fields are mandatory");
            }
        }
    }
}