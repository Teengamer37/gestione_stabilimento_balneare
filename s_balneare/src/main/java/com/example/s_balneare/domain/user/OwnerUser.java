package com.example.s_balneare.domain.user;

//TODO: da implementarla nel pattern DDD-lite, guardare i costruttori

public final class OwnerUser extends AppUser {
    public OwnerUser(Integer id, String email, String username, String name, String surname) {
        super(id, email, username, name, surname);
    }

    @Override
    public Role getRole() {
        return Role.OWNER;
    }
}