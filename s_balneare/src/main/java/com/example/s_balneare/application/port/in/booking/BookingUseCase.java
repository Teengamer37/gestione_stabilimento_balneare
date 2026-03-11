package com.example.s_balneare.application.port.in.booking;

import com.example.s_balneare.application.service.booking.BookingService;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;

//TODO: aggiungere possibilità di far prenotazioni da parte della balneazione per persone che telefonano allo stabilimento
/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare un oggetto di tipo Booking
 * Implementata in:
 * @see BookingService BookingService
 */
public interface BookingUseCase {
    Integer addBooking(Booking booking, BookingParking availableParking);
    void confirmBooking(Integer id);
    void rejectBooking(Integer id);
    void cancelBooking(Integer id);
    void addExtras (Integer id,
                    int extraSdraio, int extraLettini,
                    int extraCamerini, int extraSedie,
                    int availableSdraio, int availableLettini,
                    int availableCamerini, int availableSedie);
    void addExtraParking(Integer id, BookingParking extraParking, BookingParking availableParking);
}