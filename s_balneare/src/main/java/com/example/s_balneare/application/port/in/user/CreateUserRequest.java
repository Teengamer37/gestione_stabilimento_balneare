package com.example.s_balneare.application.port.in.user;

public abstract class CreateUserRequest {
    private final String email;
    private final String username;
    private final String name;
    private final String surname;

    public CreateUserRequest(String email, String username, String name, String surname, String phoneNumber, boolean active) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.surname = surname;
    }

    public String getUsername() {return username;}

    public String getEmail() {return email;}

    public String getName() {return name;}

    public String getSurname() {return surname;}
}
