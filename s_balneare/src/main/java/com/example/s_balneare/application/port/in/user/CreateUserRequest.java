package com.example.s_balneare.application.port.in.user;

/**
 * Rappresenta una richiesta astratta per la creazione di un User.<br>
 * Contiene attributi e metodi comuni per la creazione di diversi tipi di User.
 *
 * @see CreateCustomerRequest CreateCustomerRequest
 * @see CreateOwnerRequest CreateOwnerRequest
 * @see CreateAdminRequest CreateAdminRequest
 */
public abstract class CreateUserRequest {
    private final String email;
    private final String username;
    private final String name;
    private final String surname;

    public CreateUserRequest(String email, String username, String name, String surname) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
}