package com.example.s_balneare.domain.beach;

public class BeachGeneral {
    private final String name;
    private final String description;
    private final String telephoneNumber;

    //costruttore
    public BeachGeneral(String name, String description, String telephoneNumber) {
        validateName(name);
        validateDescription(description);
        validateTelephoneNumber(telephoneNumber);

        this.name = name;
        this.description = description;
        this.telephoneNumber = telephoneNumber;
    }

    //getters
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    //metodi wither
    public BeachGeneral withName(String name) {
        validateName(name);
        return new BeachGeneral(name, description, telephoneNumber);
    }
    public BeachGeneral withDescription(String description) {
        validateDescription(description);
        return new BeachGeneral(name, description, telephoneNumber);
    }
    public BeachGeneral withTelephoneNumber(String telephoneNumber) {
        validateTelephoneNumber(telephoneNumber);
        return new BeachGeneral(name, description, telephoneNumber);
    }

    //metodi di validazione privati
    private void validateName(String name) {
        if (name.isBlank()) throw new IllegalArgumentException("ERROR: name cannot be blank");
        if (name.length() > 100) throw new IllegalArgumentException("ERROR: name cannot exceed 100 characters");
    }
    private void validateDescription(String description) {
        if (description.length() > 512) throw new IllegalArgumentException("ERROR: description cannot exceed 512 characters");
    }
    private void validateTelephoneNumber(String telephoneNumber) {
        if (telephoneNumber == null) throw new IllegalArgumentException("ERROR: telephoneNumber cannot be null");
        String cleaned = telephoneNumber.replaceAll("\\s", "");
        if (cleaned.isBlank()) throw new IllegalArgumentException("ERROR: telephoneNumber cannot be blank");
        if (cleaned.length() > 50) throw new IllegalArgumentException("ERROR: telephoneNumber cannot exceed 50 characters");
        if (!cleaned.matches("^\\+\\d+$")) throw new IllegalArgumentException("ERROR: telephoneNumber not valid");
    }
}