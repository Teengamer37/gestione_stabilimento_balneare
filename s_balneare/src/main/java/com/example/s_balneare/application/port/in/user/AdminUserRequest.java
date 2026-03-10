package com.example.s_balneare.application.port.in.user;

public class AdminUserRequest extends AppUserRequest{
    public AdminUserRequest(String email, String username, String name, String surname, String phoneNumber, boolean active) {
        super(email, username, name, surname, phoneNumber, active);
    }
}
