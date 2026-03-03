package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.user.Role;

public class RegistrationRequest {
    // 1. Attributi comuni a tutti gli utenti
    private final Role type;
    private final String email;
    private final String username;
    private final String name;
    private final String surname;

    // 2. Attributi esclusivi del customer
    private final String phoneNumber;
    private final int addressId;

    // 3. Attributo esclusivo dell'owner
    private final int beachId;

    // Costruttore privato: l'oggetto può essere creato solo tramite il Builder
    private RegistrationRequest(Builder builder) {
        this.type = builder.type;
        this.email = builder.email;
        this.username = builder.username;
        this.name = builder.name;
        this.surname = builder.surname;
        this.phoneNumber = builder.phoneNumber;
        this.addressId = builder.addressId;
        this.beachId = builder.beachId;
    }

    // Getter (Sola lettura per garantire l'immutabilità)
    public Role getType() { return type; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getAddressId() { return addressId; }
    public int getBeachId() { return beachId; }

    // --- INNER CLASS BUILDER ---
    public static class Builder {
        // Campi del builder (stessi della classe esterna)
        private Role type;
        private String email;
        private String username;
        private String name;
        private String surname;
        private String phoneNumber;
        private Integer addressId = null; // Default a 0
        private Integer beachId = null;   // Default a 0

        // Il costruttore del Builder richiede i parametri minimi obbligatori per OGNI utente
        public Builder(Role type, String email, String username) {
            this.type = type;
            this.email = email;
            this.username = username;
        }

        public Builder withName(String name, String surname) {
            this.name = name;
            this.surname = surname;
            return this;
        }

        public Builder withCustomerData(String phoneNumber, int addressId) {
            this.phoneNumber = phoneNumber;
            this.addressId = addressId;
            return this;
        }

        public Builder withOwnerData(int beachId) {
            this.beachId = beachId;
            return this;
        }

        // Metodo build: Valida e restituisce la Request
        public RegistrationRequest build() {
            return new RegistrationRequest(this);
        }
    }
}