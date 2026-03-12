package com.example.s_balneare.domain.user;


public final class Admin extends User {
    private boolean OTP;

    public Admin(Integer id, String email, String username, String name, String surname, boolean OTP) {
        super(id, email, username, name, surname);
        this.OTP = OTP;
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public boolean isOTP() {
        return OTP;
    }

    public void updateOTP(boolean OTP) {
        this.OTP = OTP;
    }
}