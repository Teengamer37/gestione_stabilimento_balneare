package com.example.s_balneare.application.port.in.booking;

import java.time.LocalDate;
import java.util.List;

/**
 * Record che prende come parametri tutti gli attributi di Booking e BookingParking per la creazione di un Booking da parte del Customer.
 * Usato in:
 *
 * @see CreateBookingUseCase CreateBookingUseCase
 */
public record CreateBookingCommand(
        Integer beachId,
        Integer customerId,
        LocalDate date,
        List<Integer> spotIds,
        int autoPark,
        int motoPark,
        int bikePark,
        int electricPark,
        int extraSdraio,
        int extraLettini,
        int extraSedie,
        int camerini
) {
    public CreateBookingCommand {
        if (beachId == null) throw new IllegalArgumentException("ERROR: beachId cannot be null");
        if (customerId == null) throw new IllegalArgumentException("ERROR: customerId cannot be null");
        if (date == null) throw new IllegalArgumentException("ERROR: date cannot be null");
        if (spotIds == null) throw new IllegalArgumentException("ERROR: cannot create Booking without spots");
        if (spotIds.isEmpty()) throw new IllegalArgumentException("ERROR: cannot create Booking without spots");
        if (autoPark < 0 || motoPark < 0 || bikePark < 0 || electricPark < 0)
            throw new IllegalArgumentException("ERROR: parking spaces cannot be negative");
        if (extraSdraio < 0 || extraLettini < 0 || extraSedie < 0 || camerini < 0)
            throw new IllegalArgumentException("ERROR: extra quantities cannot be negative");
    }
}