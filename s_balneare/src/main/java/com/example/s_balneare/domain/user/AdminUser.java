package com.example.s_balneare.domain.user;

public final class AdminUser extends AppUser {
    public AdminUser(int id, String email, String username, String name, String surname, boolean active) {
        super(id, email, username, name, surname, active);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}