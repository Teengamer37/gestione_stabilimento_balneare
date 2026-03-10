package com.example.s_balneare.domain.user;


public final class Admin extends User {
    public Admin(Integer id, String email, String username, String name, String surname) {
        super(id, email, username, name, surname);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}