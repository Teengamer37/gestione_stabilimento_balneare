package com.example.s_balneare.domain.user;

public abstract class User {
    private final Integer id;
    private String email;
    private String username;
    private String name;
    private String surname;

    protected User(Integer id, String email, String username, String name, String surname) {
        checkEmail(email);
        checkName(name);
        checkSurname(surname);
        checkUsername(username);
        this.id = id;
        this.email = email;
        this.username = username;
        this.name = name;
        this.surname = surname;
    }

    public abstract Role getRole();

    //metodi di Business
    public void updateEmail(String email) {
        checkEmail(email);
        this.email = email;
    }

    public void updateName(String name) {
        checkName(name);
        this.name = name;
    }

    public void updateSurname(String surname){
        checkSurname(surname);
        this.surname = surname;
    }

    public void updateUsername(String username) {
        checkUsername(username);
        this.username = username;
    }

    //getters
    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getSurname() { return surname; }

    //metodi di controllo integrità dati
    private void checkEmail(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("ERROR: missing email");
        if (email.length() > 80 || email.length() < 6)  throw new IllegalArgumentException("ERROR: invalid email length");
        if (!email.contains("@") || email.contains("..")) throw new IllegalArgumentException("ERROR: invalid email address");
    }

    private void checkName(String name){
        if(name == null || name.isBlank()) throw new IllegalArgumentException("ERROR: invalid name");
        if (name.length() > 100)  throw new IllegalArgumentException("ERROR: name cannot exceed 100 characters");
    }

    private void checkSurname(String surname){
        if(surname == null || surname.isBlank()) throw new IllegalArgumentException("ERROR: invalid surname");
        if (surname.length() > 50)  throw new IllegalArgumentException("ERROR: surname cannot exceed 50 characters");
    }

    private void checkUsername(String username){
        if(username == null || username.isBlank()) throw new IllegalArgumentException("ERROR: invalid username");
        if (username.length() > 50)  throw new IllegalArgumentException("ERROR: username cannot exceed 50 characters");
    }
}