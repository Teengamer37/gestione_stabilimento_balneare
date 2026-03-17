package com.example.s_balneare.domain.booking;

/// Definisce i posti parcheggio prenotati
public record BookingParking(
        int autoPark,
        int motoPark,
        int electricPark
) {
    public BookingParking {
        if (autoPark < 0 || motoPark < 0 || electricPark < 0)
            throw new IllegalArgumentException("ERROR: parking spaces cannot be negative");
    }

    //metodo per inizializzare l'oggetto con valori di default
    public static BookingParking empty() {
        return new BookingParking(0, 0, 0);
    }

    //metodo per usare il pattern Builder per creare/manipolare BookingParking
    public static BookingParking.Builder builder() {
        return new BookingParking.Builder();
    }
    public static BookingParking.Builder builder(BookingParking bookingParking) {
        return new BookingParking.Builder(bookingParking);
    }

    //metodi wither
    public BookingParking withAutoPark(int autoPark) {
        return new BookingParking(autoPark, motoPark, electricPark);
    }
    public BookingParking withMotoPark(int motoPark) {
        return new BookingParking(autoPark, motoPark, electricPark);
    }
    public BookingParking withElectricPark(int electricPark) {
        return new BookingParking(autoPark, motoPark, electricPark);
    }

    //pattern Builder
    public static class Builder {
        private int autoPark = 0;
        private int motoPark = 0;
        private int electricPark = 0;

        public Builder() {
        }

        //costruttore copia
        public Builder(BookingParking original) {
            this.autoPark = original.autoPark;
            this.motoPark = original.motoPark;
            this.electricPark = original.electricPark;
        }

        public BookingParking.Builder autoPark(int val) {
            autoPark = val;
            return this;
        }

        public BookingParking.Builder motoPark(int val) {
            motoPark = val;
            return this;
        }

        public BookingParking.Builder electricPark(int val) {
            electricPark = val;
            return this;
        }

        public BookingParking build() {
            return new BookingParking(autoPark, motoPark, electricPark);
        }
    }
}