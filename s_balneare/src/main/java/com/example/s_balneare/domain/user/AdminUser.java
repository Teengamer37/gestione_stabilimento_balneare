package com.example.s_balneare.domain.user;

//TODO: da implementarla nel pattern DDD-lite, guardare i costruttori


public final class AdminUser extends AppUser {
    public AdminUser(int id, String email, String username, String name, String surname, boolean active) {
        super(id, email, username, name, surname);
    }

    public AdminUser(Integer id){super(id);}

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}