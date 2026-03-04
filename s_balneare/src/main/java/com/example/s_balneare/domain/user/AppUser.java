package com.example.s_balneare.domain.user;

public abstract class AppUser {
    private final Integer id;
    private String email;
    private String username;
    private String name;
    private String surname;

    protected AppUser(Integer id, String email, String username, String name, String surname) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("ERROR: invalid email");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("ERROR: username required");
        if (name == null || surname == null) throw new IllegalArgumentException("ERROR: personal data required");

        this.id = id;
        this.email = email;
        this.username = username;
        this.name = name;
        this.surname = surname;
    }

    public abstract Role getRole();

    //metodi di Business
    public void changeEmail(String newEmail) {
        if (newEmail == null || !newEmail.contains("@")) throw new IllegalArgumentException("ERROR: invalid email");
        this.email = newEmail;
    }

    public void updateProfile(String name, String surname) {
        if (name == null || surname == null) throw new IllegalArgumentException("ERROR: invalid name and/or surname");
        this.name = name;
        this.surname = surname;
    }

    //getters
    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
}