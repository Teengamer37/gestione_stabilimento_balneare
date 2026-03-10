package com.example.s_balneare.application.port.in.user;

public class CreateAdminRequest extends CreateUserRequest {
    public CreateAdminRequest(String email, String username, String name, String surname, String phoneNumber, boolean active) {
        super(email, username, name, surname, phoneNumber, active);
    }
}
