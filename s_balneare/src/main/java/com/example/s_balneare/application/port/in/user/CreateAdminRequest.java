package com.example.s_balneare.application.port.in.user;

public class CreateAdminRequest extends CreateUserRequest {
    public CreateAdminRequest(String email, String username, String name, String surname) {
        super(email, username, name, surname);
    }
}