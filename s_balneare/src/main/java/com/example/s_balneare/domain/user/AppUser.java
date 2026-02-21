package com.example.s_balneare.domain.user;

public abstract class AppUser {
    private final int id;
    private String email;
    private String username;
    private boolean active;

    protected AppUser(int id, String email, String username, boolean active) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.active = active;
    }

    public abstract Role getRole();

    public int getId() {
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