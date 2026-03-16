package com.example.s_balneare.domain.user;

/// Rappresenta un cliente del sistema
public final class Customer extends User {
    private final Integer addressId;
    private String phoneNumber;
    private boolean active;

    //costruttore compatto per assicurarsi l'integrità dei valori
    public Customer(Integer id, String email, String username, String name, String surname,
                    String phoneNumber, Integer addressId, boolean active) {
        super(id, email, username, name, surname);
        this.phoneNumber = validatePhoneNumber(phoneNumber);
        this.addressId = addressId;
        this.active = active;
    }

    //getters
    @Override
    public Role getRole() {
        return Role.CUSTOMER;
    }
    @Override
    public boolean isOTP() {
        return false;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public int getAddressId() {
        return addressId;
    }
    public boolean isActive() {
        return active;
    }

    //---- METODI BUSINESS ----

    /**
     * Cambia il numero di telefono del Customer (con controlli).
     *
     * @param newPhone Nuovo numero di telefono
     */
    public void changePhoneNumber(String newPhone) {
        validatePhoneNumber(newPhone);
        this.phoneNumber = newPhone;
    }

    /**
     * Chiude l'account Customer definitivamente.
     */
    public void closeAccount() {
        this.active = false;
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