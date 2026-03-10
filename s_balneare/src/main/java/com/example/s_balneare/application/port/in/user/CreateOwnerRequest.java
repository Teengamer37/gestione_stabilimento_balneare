package com.example.s_balneare.application.port.in.user;

public class CreateOwnerRequest extends CreateUserRequest {
    public CreateOwnerRequest(String email, String username, String name, String surname, String phoneNumber, boolean active) {
        super(email, username, name, surname, phoneNumber, active);
    }
}
