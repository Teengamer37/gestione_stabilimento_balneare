package com.example.s_balneare.domain.user;

/// Rappresenta un utente con privilegi di amministratore
public final class Admin extends User {
    private boolean OTP;

    //costruttore compatto per assicurarsi l'integrità dei valori
    public Admin(Integer id, String email, String username, String name, String surname, boolean OTP) {
        super(id, email, username, name, surname);
        this.OTP = OTP;
    }

    //getters
    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
    @Override
    public boolean isOTP() {
        return OTP;
    }

    /**
     * Attiva/disattiva OTP per forzare il cambio password.
     *
     * @param OTP nuovo stato OTP
     */
    public void updateOTP(boolean OTP) {
        this.OTP = OTP;
    }
}