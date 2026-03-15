package com.example.s_balneare.application.port.in.booking;

import com.example.s_balneare.application.service.booking.CreateManualBookingService;

/**
 * Interfaccia che definisce le funzioni utili per implementare lo Use Case di creazione Booking da parte dell'Owner.
 * <p>Essa ha bisogno di un oggetto Parking e di un oggetto Beach.
 * <p>Implementata in:
 *
 * @see CreateManualBookingService CreateManualBookingService
 */
public interface CreateManualBookingUseCase {
    Integer createManualBooking(CreateManualBookingCommand command);
}