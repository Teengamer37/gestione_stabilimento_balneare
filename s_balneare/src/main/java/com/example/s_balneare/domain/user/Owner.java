package com.example.s_balneare.domain.user;


//TODO: implementare flag primo login per cambio password obbligatorio
public final class Owner extends User {
    public Owner(Integer id, String email, String username, String name, String surname) {
        super(id, email, username, name, surname);
    }

    @Override
    public Role getRole() {
        return Role.OWNER;
    }
}