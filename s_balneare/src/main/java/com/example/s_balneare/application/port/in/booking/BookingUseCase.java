package com.example.s_balneare.application.port.in.booking;

import com.example.s_balneare.application.service.booking.BookingService;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;

import java.util.List;

//TODO: aggiungere possibilità di far prenotazioni da parte della balneazione per persone che telefonano allo stabilimento
/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare un oggetto di tipo Booking
 * Implementata in:
 * @see BookingService BookingService
 */
public interface BookingUseCase {
    void updateBooking(Integer id, List<Integer> newSpotIds, BookingParking newParking,
                       int newSdraio, int newLettini, int newSedie, int newCamerini);
    void confirmBooking(Integer id);
    void rejectBooking(Integer id);
    void cancelBooking(Integer id);
    List<Booking> getCustomerBookings(Integer customerId);
    List<Booking> getBeachBookings(Integer ownerId);
}