package com.example.s_balneare.application.port.in.booking;

/**
 * Interfaccia che definisce le funzioni utili per implementare lo Use Case di creare una prenotazione.
 * Essa ha bisogno di un oggetto Parking e di un oggetto Beach.
 * Implementata in:
 * @see com.example.s_balneare.application.service.beach.CreateBeachService CreateBeachService
 */
public interface CreateBookingUseCase {
    Integer createBooking(CreateBookingCommand command);
}