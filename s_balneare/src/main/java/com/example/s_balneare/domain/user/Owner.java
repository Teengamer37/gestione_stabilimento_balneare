package com.example.s_balneare.domain.user;

/// Rappresenta il proprietario di una spiaggia
public final class Owner extends User {
    private boolean active;
    private boolean OTP;

    //costruttore compatto per assicurarsi l'integrità dei valori
    public Owner(Integer id, String email, String username, String name, String surname, boolean active, boolean OTP) {
        super(id, email, username, name, surname);
        this.active = active;
        this.OTP = OTP;
    }

    //getters
    @Override
    public Role getRole() {
        return Role.OWNER;
    }
    @Override
    public boolean isOTP() {
        return OTP;
    }
    public boolean isActive() {
        return active;
    }

    //---- METODI BUSINESS ----

    /**
     * Attiva/disattiva OTP per forzare il cambio password.
     *
     * @param OTP nuovo stato OTP
     */
    public void updateOTP(boolean OTP) {
        this.OTP = OTP;
    }

    /**
     * Chiude l'account Owner definitivamente.
     */
    public void closeAccount() {
        this.active = false;
    }
}