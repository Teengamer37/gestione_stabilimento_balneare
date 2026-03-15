package com.example.s_balneare.application.port.in.booking;

import com.example.s_balneare.application.service.booking.CreateBookingService;

/**
 * Interfaccia che definisce le funzioni utili per implementare lo Use Case di creazione di una prenotazione.
 * Implementata in:
 *
 * @see CreateBookingService CreateBookingService
 */
public interface CreateBookingUseCase {
    Integer createBooking(CreateBookingCommand command);
}