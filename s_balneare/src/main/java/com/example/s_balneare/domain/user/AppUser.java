package com.example.s_balneare.domain.user;

/*
    FIXME: aggiungere controlli dimensione dati stringhe:

    esempio estratto da Address:
    if (street.length() > 255) throw new IllegalArgumentException("ERROR: street cannot exceed 255 characters");
    if (streetNumber.length() > 10) throw new IllegalArgumentException("ERROR: streetNumber cannot exceed 10 characters");
    if (city.length() > 100) throw new IllegalArgumentException("ERROR: city cannot exceed 100 characters");
    if (zipCode.length() > 20) throw new IllegalArgumentException("ERROR: zip code cannot exceed 20 characters");
    if (country.length() > 100) throw new IllegalArgumentException("ERROR: country cannot exceed 100 characters");
 */

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