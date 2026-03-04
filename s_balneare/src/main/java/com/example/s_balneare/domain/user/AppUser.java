package com.example.s_balneare.domain.user;

//TODO: da implementarla nel pattern DDD-lite, guardare i costruttori

public abstract class AppUser {
    private final Integer id;
    private String email;
    private String username;
    private String name;
    private String surname;

    protected AppUser(int id, String email, String username, String name, String surname) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.name = name;
        this.surname = surname;
    }

    protected AppUser(Integer id){
        this.id = id;
    }

    public abstract Role getRole();

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

}