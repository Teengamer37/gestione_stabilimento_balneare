package com.example.s_balneare.application.port.in.user;

/**
 * Rappresenta una richiesta per la creazione di un Customer.
 * <p>Estende la classe astratta CreateUserRequest e aggiunge gli attributi specifici di un Customer.
 *
 * @see CreateUserRequest CreateUserRequest
 */
public class CreateCustomerRequest extends CreateUserRequest {
    private final String phoneNumber;
    private final Boolean active;
    private final String street;
    private final String streetNumber;
    private final String city;
    private final String zipCode;
    private final String country;

    public CreateCustomerRequest(
            String email,
            String username,
            String name,
            String surname,
            String phoneNumber,
            Boolean active,
            String street,
            String streetNumber,
            String city,
            String zipCode,
            String country) {
        super(email, username, name, surname);
        this.phoneNumber = phoneNumber;
        this.active = active;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getStreet() {
        return street;
    }
    public String getCity() {
        return city;
    }
    public String getStreetNumber() {
        return streetNumber;
    }
    public String getZipCode() {
        return zipCode;
    }
    public String getCountry() {
        return country;
    }
    public boolean isActive() {
        return active;
    }
}