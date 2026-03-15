package com.example.s_balneare.application.port.in.user;

/**
 * Rappresenta una richiesta per la creazione di un Owner.
 * Estende la classe astratta CreateUserRequest e aggiunge gli attributi specifici di un Owner (per ora nessuno).
 *
 * @see CreateUserRequest CreateUserRequest
 */
public class CreateOwnerRequest extends CreateUserRequest {
    public CreateOwnerRequest(String email, String username, String name, String surname) {
        super(email, username, name, surname);
    }
}