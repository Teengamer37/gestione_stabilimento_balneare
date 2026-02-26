package com.example.s_balneare.application.service;

import com.example.s_balneare.application.port.out.BookingRepository;
import com.example.s_balneare.domain.booking.Booking;

import java.util.List;

//contiene metodi per gestire la collezione di bookings salvati nel database
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    //aggiunta booking nel DB
    public int addBooking(Booking booking) {
        //check spot occupati
        List<Integer> occupiedSpots = bookingRepository.findOccupiedSpots(booking.getBeachId(), booking.getDate());

        for (int spotId : booking.getSpotIds()) {
            if (occupiedSpots.contains(spotId)) {
                throw new IllegalArgumentException("ERROR: Spot " + spotId + " is already occupied on " + booking.getDate());
            }
        }

        return bookingRepository.save(booking);
    }

    //ricerca, aggiorna stato in CONFIRMED e salva booking nel DB
    public void confirmBooking(int id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Booking not found"));

        booking.confirmBooking();

        bookingRepository.update(booking);
    }

    //ricerca, aggiorna stato in REJECTED e salva booking nel DB
    public void rejectBooking(int id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Booking not found"));

        booking.rejectBooking();

        bookingRepository.update(booking);
    }

    //ricerca, aggiorna stato in CANCELLED e salva booking nel DB
    public void cancelBooking(int id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Booking not found"));

        booking.cancelBooking();

        bookingRepository.update(booking);
    }

    //ricerca, aggiorna gli extra e salva booking nel DB
    public void addExtras (int id,
                           int extraSdraio, int extraLettini,
                           int extraCamerini, int extraSedie,
                           int availableSdraio, int availableLettini,
                           int availableCamerini, int availableSedie) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Booking not found"));

        if (extraSdraio > 0) booking.addExtraSdraio(extraSdraio, availableSdraio);
        if (extraLettini > 0) booking.addExtraLettini(extraLettini, availableLettini);
        if (extraCamerini > 0) booking.addCamerini(extraCamerini, availableCamerini);
        if (extraSedie > 0) booking.addExtraSedie(extraSedie, availableSedie);

        bookingRepository.update(booking);
    }
}