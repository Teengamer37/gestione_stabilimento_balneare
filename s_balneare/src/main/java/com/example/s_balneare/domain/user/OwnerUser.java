package com.example.s_balneare.domain.user;

import com.example.s_balneare.domain.beach.Beach;

import java.util.UUID;

public final class OwnerUser extends AppUser {
    private int beachId;

    public OwnerUser(int id, String email, String username, String name, String surname, boolean active, int beachId) {
        super(id, email, username, name, surname, active);
        this.beachId = beachId;
    }

    @Override
    public Role getRole() {
        return Role.OWNER;
    }

    public int getBeachId() {
        return beachId;
    }

    public void setBeachId(int beachId) {
        this.beachId = beachId;
    }
}