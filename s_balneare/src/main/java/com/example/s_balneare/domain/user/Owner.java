package com.example.s_balneare.domain.user;


//TODO: implementare flag primo login per cambio password obbligatorio
public final class Owner extends User {
    private boolean OTP;

    public Owner(Integer id, String email, String username, String name, String surname, boolean OTP) {
        super(id, email, username, name, surname);
        this.OTP = OTP;
    }

    @Override
    public Role getRole() {
        return Role.OWNER;
    }

    @Override
    public boolean isOTP() {
        return OTP;
    }

    public void updateOTP(boolean OTP) {
        this.OTP = OTP;
    }
}