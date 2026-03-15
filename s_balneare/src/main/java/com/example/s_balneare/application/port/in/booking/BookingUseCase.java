package com.example.s_balneare.application.port.in.booking;

import com.example.s_balneare.application.service.booking.BookingService;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;

import java.util.List;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare un oggetto di tipo Booking.
 * <p>Implementata in:
 *
 * @see BookingService BookingService
 */
public interface BookingUseCase {
    void updateBooking(Integer id, List<Integer> newSpotIds, BookingParking newParking,
                       int newSdraio, int newLettini, int newSedie, int newCamerini);

    //update stato di Booking
    void confirmBooking(Integer id);
    void rejectBooking(Integer id);
    void cancelBooking(Integer id);

    //letture
    List<Booking> getCustomerBookings(Integer customerId);
    List<Booking> getBeachBookings(Integer ownerId);
}