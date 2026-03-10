package com.example.s_balneare.domain.user;

public final class Customer extends User {
    private String phoneNumber;
    private final Integer addressId;
    private boolean active;

    //costruttore
    public Customer(Integer id, String email, String username, String name, String surname,
                    String phoneNumber, Integer addressId, boolean active) {
        super(id, email, username, name, surname);
        this.phoneNumber = validatePhoneNumber(phoneNumber);
        this.addressId = addressId;
        this.active = active;
    }

    @Override
    public Role getRole() { return Role.CUSTOMER; }

    public void changePhoneNumber(String newPhone) {
        validatePhoneNumber(newPhone);
        this.phoneNumber = newPhone;
    }

    public void setActive(boolean active) {this.active = active;}

    public String getPhoneNumber() { return phoneNumber; }
    public int getAddressId() { return addressId; }
    public boolean isActive() { return active; }

    private String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) throw new IllegalArgumentException("ERROR: phoneNumber cannot be null");
        String cleaned = phoneNumber.replaceAll("\\s", "");
        if (cleaned.isBlank()) throw new IllegalArgumentException("ERROR: phoneNumber cannot be blank");
        if (cleaned.length() > 50) throw new IllegalArgumentException("ERROR: phoneNumber cannot exceed 50 characters");
        if (!cleaned.matches("^\\+\\d+$")) throw new IllegalArgumentException("ERROR: phoneNumber not valid");
        return cleaned;
    }
}