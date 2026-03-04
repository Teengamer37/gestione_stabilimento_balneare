package com.example.s_balneare.domain.user;

//TODO: da implementarla nel pattern DDD-lite, guardare i costruttori

import com.example.s_balneare.application.factory.OwnerFactory;

public final class OwnerUser extends AppUser {

    public OwnerUser(int id, String email, String username, String name, String surname, boolean active) {
        super(id, email, username, name, surname);
    }

    public OwnerUser(Integer id) {super(id);}

    @Override
    public Role getRole() {
        return Role.OWNER;
    }
}