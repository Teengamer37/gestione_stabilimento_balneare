package com.example.s_balneare.domain.user;

import com.example.s_balneare.domain.beach.Beach;

import java.util.UUID;

public final class OwnerUser extends AppUser {
    private final Beach beach;

    public OwnerUser(UUID id, String email, String username, boolean active, Beach beach) {
        super(id, email, username, active);
        this.beach = beach;
    }

    @Override
    public Role getRole() {
        return Role.OWNER;
    }

    public Beach getBeach() {
        return beach;
    }
}
