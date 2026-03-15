package com.example.s_balneare.application.port.in.user;

/**
 * Rappresenta una richiesta per la creazione di un Admin.
 * Estende la classe astratta CreateUserRequest e aggiunge gli attributi specifici di un Admin (per ora nessuno).
 *
 * @see CreateUserRequest CreateUserRequest
 */
public class CreateAdminRequest extends CreateUserRequest {
    public CreateAdminRequest(String email, String username, String name, String surname) {
        super(email, username, name, surname);
    }
}