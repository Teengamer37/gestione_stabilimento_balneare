package com.example.s_balneare.domain.beach;

/// Contiene gli attributi generali di una spiaggia
public record BeachGeneral(String name, String description, String phoneNumber) {
    //costruttore
    public BeachGeneral(String name, String description, String phoneNumber) {
        validateName(name);
        validateDescription(description);

        this.name = name;
        this.description = description;
        //guarda validatePhoneNumber()
        this.phoneNumber = validatePhoneNumber(phoneNumber);
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
        if (description.length() > 512)
            throw new IllegalArgumentException("ERROR: description cannot exceed 512 characters");
    }

    /**
     * L'unica validazione che restituisce il valore indietro.
     * <p>Se l'utente inserisce "+39 363 363 3633", il metodo ritorna "+393633633633" (no spazi).
     *
     * @param phoneNumber numero di telefono da verificare
     * @return numero di telefono ben formattato
     */
    private String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) throw new IllegalArgumentException("ERROR: phoneNumber cannot be null");
        String cleaned = phoneNumber.replaceAll("\\s", "");
        if (cleaned.isBlank()) throw new IllegalArgumentException("ERROR: phoneNumber cannot be blank");
        if (cleaned.length() > 50) throw new IllegalArgumentException("ERROR: phoneNumber cannot exceed 50 characters");
        if (!cleaned.matches("^\\+\\d+$")) throw new IllegalArgumentException("ERROR: phoneNumber not valid");
        return cleaned;
    }
}