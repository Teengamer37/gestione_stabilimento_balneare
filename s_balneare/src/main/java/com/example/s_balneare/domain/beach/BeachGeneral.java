package com.example.s_balneare.domain.beach;

//TODO this.phoneNumber = validatePhoneNumber(phoneNumber); ??

public class BeachGeneral {
    private final String name;
    private final String description;
    private final String phoneNumber;

    //costruttore
    public BeachGeneral(String name, String description, String phoneNumber) {
        validateName(name);
        validateDescription(description);
        //guarda validatePhoneNumber()
        String cleaned = validatePhoneNumber(phoneNumber);

        this.name = name;
        this.description = description;
        this.phoneNumber = cleaned;
    }

    //getters
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    //metodi wither
    public BeachGeneral withName(String name) {
        validateName(name);
        return new BeachGeneral(name, description, phoneNumber);
    }
    public BeachGeneral withDescription(String description) {
        validateDescription(description);
        return new BeachGeneral(name, description, phoneNumber);
    }
    public BeachGeneral withPhoneNumber(String phoneNumber) {
        String cleaned = validatePhoneNumber(phoneNumber);
        return new BeachGeneral(name, description, cleaned);
    }

    //metodi di validazione privati
    private void validateName(String name) {
        if (name.isBlank()) throw new IllegalArgumentException("ERROR: name cannot be blank");
        if (name.length() > 100) throw new IllegalArgumentException("ERROR: name cannot exceed 100 characters");
    }
    private void validateDescription(String description) {
        if (description.length() > 512) throw new IllegalArgumentException("ERROR: description cannot exceed 512 characters");
    }
    //l'unica validazione che restituisce il valore indietro
    //se l'utente inserisce "+39 363 363 3633", il metodo ritorna "+393633633633" (no spazi)
    private String validatePhoneNumber(String telephoneNumber) {
        if (telephoneNumber == null) throw new IllegalArgumentException("ERROR: phoneNumber cannot be null");
        String cleaned = telephoneNumber.replaceAll("\\s", "");
        if (cleaned.isBlank()) throw new IllegalArgumentException("ERROR: phoneNumber cannot be blank");
        if (cleaned.length() > 50) throw new IllegalArgumentException("ERROR: phoneNumber cannot exceed 50 characters");
        if (!cleaned.matches("^\\+\\d+$")) throw new IllegalArgumentException("ERROR: phoneNumber not valid");
        return cleaned;
    }
}