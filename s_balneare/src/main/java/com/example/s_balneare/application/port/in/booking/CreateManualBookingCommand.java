package com.example.s_balneare.application.port.in.booking;

import java.time.LocalDate;
import java.util.List;

/**
 * Record che prende come parametri tutti gli attributi di Booking e BookingParking nel caso di creazione Booking da parte dell'Owner.
 * <p>Usato in:
 *
 * @see CreateManualBookingUseCase CreateManualBookingUseCase
 */
public record CreateManualBookingCommand(
        Integer ownerId,
        String callerName,
        String callerPhone,
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
    public CreateManualBookingCommand {
        if (ownerId == null) throw new IllegalArgumentException("ERROR: ownerId cannot be null");
        if (callerName == null || callerName.isBlank())
            throw new IllegalArgumentException("ERROR: callerName cannot be null/blank");
        if (callerPhone == null || callerPhone.isBlank())
            throw new IllegalArgumentException("ERROR: callerPhone cannot be null/blank");
        if (date == null) throw new IllegalArgumentException("ERROR: date cannot be null");
        if (spotIds == null) throw new IllegalArgumentException("ERROR: cannot create Booking without spots");
        if (spotIds.isEmpty()) throw new IllegalArgumentException("ERROR: cannot create Booking without spots");
        if (autoPark < 0 || motoPark < 0 || bikePark < 0 || electricPark < 0)
            throw new IllegalArgumentException("ERROR: parking spaces cannot be negative");
        if (extraSdraio < 0 || extraLettini < 0 || extraSedie < 0 || camerini < 0)
            throw new IllegalArgumentException("ERROR: extra quantities cannot be negative");
    }
}