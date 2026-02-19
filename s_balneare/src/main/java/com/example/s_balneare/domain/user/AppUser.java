package com.example.s_balneare.domain.user;

import java.util.UUID;

public abstract class AppUser {
    private final UUID id;
    private String email;
    private String username;
    private boolean active;

    protected AppUser(UUID id, String email, String username, boolean active) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.active = active;
    }

    public abstract Role getRole();

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public boolean isActive() {
        return active;
    }
}
