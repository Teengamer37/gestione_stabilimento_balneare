package com.example.s_balneare.application.service;

import com.example.s_balneare.application.port.out.BookingRepository;
import com.example.s_balneare.domain.booking.Booking;

import java.util.List;

/**
 * Implementazione dell'interfaccia che permette la manipolazione della collezione di Booking tra l'app Java e il Database.
 */
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Aggiunta booking nel DB
     * @param booking Nuovo booking da aggiungere
     * @return ID del booking aggiunto generato dal Database
     */
    public int addBooking(Booking booking) {
        //check spot occupati
        List<Integer> occupiedSpots = bookingRepository.findOccupiedSpots(booking.getBeachId(), booking.getDate());

        for (Integer spotId : booking.getSpotIds()) {
            if (occupiedSpots.contains(spotId)) {
                throw new IllegalArgumentException("ERROR: Spot " + spotId + " is already occupied on " + booking.getDate());
            }
        }

        return bookingRepository.save(booking);
    }

    /**
     * Ricerca, aggiorna stato in CONFIRMED e salva booking nel DB
     * @param id Identificatore booking da manipolare nel DB
     */
    public void confirmBooking(Integer id) {
        Booking booking = getBookingOrThrow(id);

        booking.confirmBooking();

        bookingRepository.update(booking);
    }

    /**
     * Ricerca, aggiorna stato in REJECTED e salva booking nel DB
     * @param id Identificatore booking da manipolare nel DB
     */
    public void rejectBooking(Integer id) {
        Booking booking = getBookingOrThrow(id);

        booking.rejectBooking();

        bookingRepository.update(booking);
    }

    /**
     * Ricerca, aggiorna stato in CANCELLED e salva booking nel DB
     * @param id Identificatore booking da manipolare nel DB
     */
    public void cancelBooking(Integer id) {
        Booking booking = getBookingOrThrow(id);

        booking.cancelBooking();

        bookingRepository.update(booking);
    }

    /**
     * Ricerca, aggiorna gli extra e salva booking nel DB
     * @param id Identificatore booking da manipolare nel DB
     * @param extraSdraio Numero di sdraio extra da aggiungere alla prenotazione
     * @param extraLettini Numero di lettini extra da aggiungere alla prenotazione
     * @param extraCamerini Numero di camerini da aggiungere alla prenotazione
     * @param extraSedie Numero di sedie extra da aggiungere alla prenotazione
     * @param availableSdraio Numero di sdraio prenotabili nella spiaggia in quel giorno
     * @param availableLettini Numero di lettini prenotabili nella spiaggia in quel giorno
     * @param availableCamerini Numero di camerini prenotabili nella spiaggia in quel giorno
     * @param availableSedie Numero di sedie prenotabili nella spiaggia in quel giorno
     */
    public void addExtras (Integer id,
                           int extraSdraio, int extraLettini,
                           int extraCamerini, int extraSedie,
                           int availableSdraio, int availableLettini,
                           int availableCamerini, int availableSedie) {
        Booking booking = getBookingOrThrow(id);

        if (extraSdraio > 0) booking.addExtraSdraio(extraSdraio, availableSdraio);
        if (extraLettini > 0) booking.addExtraLettini(extraLettini, availableLettini);
        if (extraCamerini > 0) booking.addCamerini(extraCamerini, availableCamerini);
        if (extraSedie > 0) booking.addExtraSedie(extraSedie, availableSedie);

        bookingRepository.update(booking);
    }

    /**
     * Metodo privato che serve nelle operazioni sensibili (in questo caso in update):
     * cerca in DB -> se non trovo la spiaggia, restituisce NULL -> interrompo tutto
     * @param id Identificativo booking da cercare
     * @return oggetto Booking con quell'ID
     * @throws IllegalArgumentException se il booking non è stato trovato nel DB
     */
    private Booking getBookingOrThrow(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Booking not found with id: " + id));
    }
}