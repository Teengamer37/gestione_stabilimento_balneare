package com.example.s_balneare.domain.user;

public final class CustomerUser extends AppUser {
    private String phoneNumber;
    private Integer addressId;
    private boolean active;

    /*
        FIXME: per il phoneNumber, farei questi tipi di controllo che ho fatto in BeachGeneral:

        private void validatePhoneNumber(String phoneNumber) {
            if (phoneNumber == null) throw new IllegalArgumentException("ERROR: phoneNumber cannot be null");
            String cleaned = phoneNumber.replaceAll("\\s", "");
            if (cleaned.isBlank()) throw new IllegalArgumentException("ERROR: phoneNumber cannot be blank");
            if (cleaned.length() > 50) throw new IllegalArgumentException("ERROR: phoneNumber cannot exceed 50 characters");
            if (!cleaned.matches("^\\+\\d+$")) throw new IllegalArgumentException("ERROR: phoneNumber not valid");
        }

        il primo fa un check sulla stringa, il secondo elimina gli spazi, il terzo controlla se stringa vuota,
        il quarto se supera la lunghezza dedicata al DB, il quinto verifica se la stringa inizia col prefisso
        (ad esempio +39 - Italia)
     */

    //costruttore
    public CustomerUser(Integer id, String email, String username, String name, String surname,
                        String phoneNumber, Integer addressId, boolean active) {
        super(id, email, username, name, surname);

        if (phoneNumber == null || phoneNumber.isBlank()) throw new IllegalArgumentException("ERROR: phoneNumber must be set");

        this.phoneNumber = phoneNumber;
        this.addressId = addressId;
        this.active = active;
    }

    @Override
    public Role getRole() { return Role.CUSTOMER; }

    public void changePhoneNumber(String newPhone) {
        if (newPhone == null) throw new IllegalArgumentException("ERROR: invalid phoneNumber");
        this.phoneNumber = newPhone;
    }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }

    public String getPhoneNumber() { return phoneNumber; }
    public int getAddressId() { return addressId; }
    public boolean isActive() { return active; }
}