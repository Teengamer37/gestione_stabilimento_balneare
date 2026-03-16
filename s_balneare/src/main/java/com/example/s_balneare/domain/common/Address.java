package com.example.s_balneare.domain.common;

/// Definisce un indirizzo geografico
public record Address(
        Integer id,
        String street,
        String streetNumber,
        String city,
        String zipCode,
        String country
) {

    //controlli creazione nuovo indirizzo
    public Address {
        //check stringhe vuote/nulle
        if (street == null || street.isBlank()) throw new IllegalArgumentException("ERROR: street cannot be blank");
        if (streetNumber == null || streetNumber.isBlank())
            throw new IllegalArgumentException("ERROR: street number cannot be blank");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("ERROR: city cannot be blank");
        if (zipCode == null || zipCode.isBlank()) throw new IllegalArgumentException("ERROR: zip code cannot be blank");
        if (country == null || country.isBlank()) throw new IllegalArgumentException("ERROR: country cannot be blank");

        //check lunghezze stringhe per farle entrare nel DB
        if (street.length() > 255) throw new IllegalArgumentException("ERROR: street cannot exceed 255 characters");
        if (streetNumber.length() > 10)
            throw new IllegalArgumentException("ERROR: streetNumber cannot exceed 10 characters");
        if (city.length() > 100) throw new IllegalArgumentException("ERROR: city cannot exceed 100 characters");
        if (zipCode.length() > 20) throw new IllegalArgumentException("ERROR: zip code cannot exceed 20 characters");
        if (country.length() > 100) throw new IllegalArgumentException("ERROR: country cannot exceed 100 characters");
    }

    //Static Factory per indirizzi nuovi da inserire nel database
    public static Address create(String street, String streetNumber, String city, String zipCode, String country) {
        return new Address(0, street, streetNumber, city, zipCode, country);
    }

    //metodo per usare il pattern Builder per creare/manipolare Address
    public static Builder builder() {
        return new Builder();
    }

    //metodi wither
    public Address withId(Integer id) {
        return new Address(id, street, streetNumber, city, zipCode, country);
    }
    public Address withStreet(String street) {
        return new Address(id, street, streetNumber, city, zipCode, country);
    }
    public Address withStreetNumber(String streetNumber) {
        return new Address(id, street, streetNumber, city, zipCode, country);
    }
    public Address withCity(String city) {
        return new Address(id, street, streetNumber, city, zipCode, country);
    }
    public Address withZipCode(String zipCode) {
        return new Address(id, street, streetNumber, city, zipCode, country);
    }
    public Address withCountry(String country) {
        return new Address(id, street, streetNumber, city, zipCode, country);
    }

    //pattern Builder
    public static class Builder {
        private Integer id = 0;
        private String street;
        private String streetNumber;
        private String city;
        private String zipCode;
        private String country;

        public Builder() {
        }

        //costruttore copia
        public Builder(Address original) {
            this.id = original.id;
            this.street = original.street;
            this.streetNumber = original.streetNumber;
            this.city = original.city;
            this.zipCode = original.zipCode;
            this.country = original.country;
        }

        public Builder id(Integer val) {
            id = val;
            return this;
        }

        public Builder street(String val) {
            street = val;
            return this;
        }

        public Builder streetNumber(String val) {
            streetNumber = val;
            return this;
        }

        public Builder city(String val) {
            city = val;
            return this;
        }

        public Builder zipCode(String val) {
            zipCode = val;
            return this;
        }

        public Builder country(String val) {
            country = val;
            return this;
        }

        public Address build() {
            return new Address(id, street, streetNumber, city, zipCode, country);
        }
    }
}