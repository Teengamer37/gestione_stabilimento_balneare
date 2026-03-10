package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.booking.Booking;

//TODO: aggiungere implementazione parcheggi
//TODO: aggiungere possibilità di far prenotazioni da parte della balneazione per persone che telefonano allo stabilimento
/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare un oggetto di tipo Booking
 * Implementata in:
 * @see com.example.s_balneare.application.service.BookingService BookingService
 */
public interface BookingUseCase {
    Integer addBooking(Booking booking);
    void confirmBooking(Integer id);
    void rejectBooking(Integer id);
    void cancelBooking(Integer id);
    void addExtras (Integer id,
                    int extraSdraio, int extraLettini,
                    int extraCamerini, int extraSedie,
                    int availableSdraio, int availableLettini,
                    int availableCamerini, int availableSedie);
}