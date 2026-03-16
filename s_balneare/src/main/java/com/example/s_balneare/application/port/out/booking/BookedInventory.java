package com.example.s_balneare.application.port.out.booking;

/**
 * Record che rappresenta il numero di oggetti extra prenotati in un giorno specifico ad una specifica spiaggia.<br>
 * Usata in:
 *
 * @see AvailabilityQuery AvailabilityQuery
 */
public record BookedInventory(
        int sdraio,
        int lettini,
        int sedie,
        int camerini
) {
    public BookedInventory {
        if (sdraio < 0 || lettini < 0 || sedie < 0 || camerini < 0)
            throw new IllegalArgumentException("ERROR: booked inventory counts cannot be negative");
    }
}