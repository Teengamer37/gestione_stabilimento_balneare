package com.example.s_balneare.domain.user;

public final class CustomerUser extends AppUser {
    private String phoneNumber;
    private int addressId;
    private boolean active;

    // Costruttore atomico: riceve tutto e valida tutto
    public CustomerUser(Integer id, String email, String username, String name, String surname,
                        String phoneNumber, int addressId, boolean active) {
        super(id, email, username, name, surname);

        if (phoneNumber == null || phoneNumber.isBlank()) throw new IllegalArgumentException("Mandatory telephone number for Customer");

        this.phoneNumber = phoneNumber;
        this.addressId = addressId;
        this.active = active;
    }

    @Override
    public Role getRole() { return Role.CUSTOMER; }

    public void changePhoneNumber(String newPhone) {
        if (newPhone == null) throw new IllegalArgumentException("Invalid phone");
        this.phoneNumber = newPhone;
    }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }

    public String getPhoneNumber() { return phoneNumber; }
    public int getAddressId() { return addressId; }
    public boolean isActive() { return active; }
}