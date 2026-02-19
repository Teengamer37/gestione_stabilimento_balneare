package com.example.s_balneare.domain.user;

import java.util.UUID;

public final class AdminUser extends AppUser {
    public AdminUser(UUID id, String email, String username, boolean active) {
        super(id, email, username, active);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
