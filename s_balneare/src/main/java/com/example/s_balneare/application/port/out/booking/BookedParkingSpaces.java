package com.example.s_balneare.application.port.out.booking;

/**
 * Record che rappresenta il numero di parcheggi prenotati in un giorno specifico ad una specifica spiaggia.
 */
public record BookedParkingSpaces(
        int bookedAuto,
        int bookedMoto,
        int bookedBike,
        int bookedElectric
) {
    public BookedParkingSpaces {
        if (bookedAuto < 0 || bookedMoto < 0 || bookedBike < 0 || bookedElectric < 0)
            throw new IllegalArgumentException("Booked parking spaces cannot be negative");
    }
}