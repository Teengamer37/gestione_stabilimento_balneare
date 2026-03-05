package com.example.s_balneare.domain.user;


public final class AdminUser extends AppUser {
    public AdminUser(Integer id, String email, String username, String name, String surname) {
        super(id, email, username, name, surname);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}