package com.example.s_balneare.domain.user;

public final class Owner extends User {
    private boolean active;
    private boolean OTP;

    public Owner(Integer id, String email, String username, String name, String surname, boolean active, boolean OTP) {
        super(id, email, username, name, surname);
        this.active = active;
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

    public boolean isActive() {return active;}

    public void closeAccount() {this.active = false;}

}