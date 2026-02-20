package com.example.s_balneare.domain.user;

import com.example.s_balneare.domain.beach.Beach;

import java.util.UUID;

public final class OwnerUser extends AppUser {
    private UUID beachID;

    public OwnerUser(UUID id, String email, String username, boolean active, UUID beachID) {
        super(id, email, username, active);
        this.beachID = beachID;
    }

    @Override
    public Role getRole() {
        return Role.OWNER;
    }

    public UUID getBeachID() {
        return beachID;
    }

    public void setBeachID(UUID beachID) {
        this.beachID = beachID;
    }
}
