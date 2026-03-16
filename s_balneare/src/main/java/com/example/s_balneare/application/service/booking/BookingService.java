package com.example.s_balneare.application.service.booking;

import com.example.s_balneare.application.port.in.booking.BookingUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.AvailabilityQuery;
import com.example.s_balneare.application.port.out.booking.BookedInventory;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.BeachInventory;
import com.example.s_balneare.domain.beach.Parking;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;
import com.example.s_balneare.domain.booking.PriceCalculator;
import com.example.s_balneare.domain.common.TransactionContext;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementazione dell'interfaccia che permette la manipolazione della collezione di Booking tra l'app Java e il Database.<br>
 * Usa BookingRepository per compiere le varie operazioni di aggiornamento sulla prenotazione;<br>
 * Usa BeachRepository per prendere riferimenti vari dalla spiaggia registrata nella prenotazione;<br>
 * Usa AvailabilityQuery per controllare le varie disponibilità sui parcheggi/oggetti extra.<br>
 * Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see BookingUseCase BookingUseCase
 * @see BookingRepository BookingRepository
 * @see BeachRepository BeachRepository
 * @see AvailabilityQuery AvailabilityQuery
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class BookingService implements BookingUseCase {
    private final BookingRepository bookingRepository;
    private final BeachRepository beachRepository;
    private final AvailabilityQuery availabilityQuery;
    private final TransactionManager transactionManager;

    public BookingService(BookingRepository bookingRepository, TransactionManager transactionManager,
                          BeachRepository beachRepository, AvailabilityQuery availabilityQuery) {
        this.bookingRepository = bookingRepository;
        this.transactionManager = transactionManager;
        this.beachRepository = beachRepository;
        this.availabilityQuery = availabilityQuery;
    }

    /**
     * Aggiorna booking nel DB con nuovi dettagli.
     *
     * @param id          ID del Booking da modificare
     * @param newSpotIds  Spot prenotati da aggiornare nel Booking
     * @param newParking  Parcheggi da aggiornare al Booking
     * @param newSdraio   Extra sdraio da aggiornare al Booking
     * @param newLettini  Extra lettini da aggiornare al Booking
     * @param newSedie    Extra sedie da aggiornare al Booking
     * @param newCamerini Extra camerini da aggiornare al Booking
     */
    @Override
    public void updateBooking(Integer id, List<Integer> newSpotIds, BookingParking newParking,
                              int newSdraio, int newLettini, int newSedie, int newCamerini) {
        transactionManager.executeInTransaction(context -> {
            //passo 1: recupero la prenotazione dal DB
            Booking booking = getBookingOrThrow(id, context);
            Beach beach = beachRepository.findById(booking.getBeachId(), context).get();

            //passo 2: check sulla data per vedere se è possibile modificare la prenotazione
            if (!booking.getDate().isAfter(LocalDate.now())) {
                throw new IllegalStateException("ERROR: booking cannot be updated on or after its date");
            }

            //passo 3: calcolo il numero di posti parcheggio ed extra oggetti prenotati finora
            BookedParkingSpaces bookedParking = availabilityQuery.getBookedParking(beach.getId(), booking.getDate(), id, context);
            BookedInventory bookedInv = availabilityQuery.getBookedInventory(beach.getId(), booking.getDate(), id, context);

            //passo 4: controllo disponibilità parcheggi
            if (!isParkingAvailable(beach.getParking(), bookedParking, newParking)) {
                throw new IllegalStateException("ERROR: too many parking spaces selected");
            }

            //passo 5: controllo disponibilità extra oggetti
            BeachInventory cap = beach.getBeachInventory();
            if ((cap.countExtraSdraio() - bookedInv.sdraio() < newSdraio) ||
                    (cap.countExtraLettini() - bookedInv.lettini() < newLettini) ||
                    (cap.countExtraSedie() - bookedInv.sedie() < newSedie) ||
                    (cap.countCamerini() - bookedInv.camerini() < newCamerini)) {
                throw new IllegalStateException("ERROR: not enough inventory items available");
            }

            //passo 6: verifico se gli spot appartengono effettivamente alla spiaggia
            if (!beachRepository.doSpotsBelongToBeach(beach.getId(), newSpotIds, context)) {
                throw new SecurityException("ERROR: one or more spots do not belong to the beach");
            }

            //passo 7: aggiornamento e salvataggio nel DB
            booking.updateExtraSdraio(newSdraio);
            booking.updateExtraLettini(newLettini);
            booking.updateExtraSedie(newSedie);
            booking.updateCamerini(newCamerini);
            booking.updateSpotsAndParking(newSpotIds, newParking);
            booking.updateTotalPrice(PriceCalculator.calculateTotal(booking, beach));
            bookingRepository.update(booking, context);
        });
    }

    /**
     * Ricerca, aggiorna stato in CONFIRMED e salva booking nel DB.
     *
     * @param id Identificatore booking da manipolare nel DB
     */
    @Override
    public void confirmBooking(Integer id) {
        transactionManager.executeInTransaction(context -> {
            Booking booking = getBookingOrThrow(id, context);

            //check sulla data per vedere se è possibile modificare la prenotazione
            if (!booking.getDate().isAfter(LocalDate.now())) {
                throw new IllegalStateException("ERROR: booking cannot be confirmed on or after its date");
            }

            booking.confirmBooking();

            bookingRepository.update(booking, context);
        });
    }

    /**
     * Ricerca, aggiorna stato in REJECTED e salva booking nel DB.
     *
     * @param id Identificatore booking da manipolare nel DB
     */
    @Override
    public void rejectBooking(Integer id) {
        transactionManager.executeInTransaction(context -> {
            Booking booking = getBookingOrThrow(id, context);

            //check sulla data per vedere se è possibile modificare la prenotazione
            if (!booking.getDate().isAfter(LocalDate.now())) {
                throw new IllegalStateException("ERROR: booking cannot be rejected on or after its date");
            }

            booking.rejectBooking();

            bookingRepository.update(booking, context);
        });
    }

    /**
     * Ricerca, aggiorna stato in CANCELLED e salva booking nel DB.
     *
     * @param id Identificatore booking da manipolare nel DB
     */
    @Override
    public void cancelBooking(Integer id) {
        transactionManager.executeInTransaction(context -> {
            Booking booking = getBookingOrThrow(id, context);

            //check sulla data per vedere se è possibile modificare la prenotazione
            if (!booking.getDate().isAfter(LocalDate.now())) {
                throw new IllegalStateException("ERROR: booking cannot be cancelled on or after its date");
            }

            booking.cancelBooking();

            bookingRepository.update(booking, context);
        });
    }

    /**
     * Prende dal database una lista di Booking fatti da un Customer (indipendentemente dallo stato dei singoli Booking).
     *
     * @param customerId ID del Customer da cercare nel DB
     * @return Lista di Booking effettuati da quel Customer
     */
    @Override
    public List<Booking> getCustomerBookings(Integer customerId) {
        return transactionManager.executeInTransaction(context -> {
            return bookingRepository.findByCustomerId(customerId, context);
        });
    }

    /**
     * Prende dal database una lista di Booking fatti per una determinata Beach (indipendentemente dallo stato dei singoli Booking).
     *
     * @param ownerId ID dell'Owner per poi cercare la spiaggia associata
     * @return Lista di Booking registrati nella spiaggia
     */
    @Override
    public List<Booking> getBeachBookings(Integer ownerId) {
        return transactionManager.executeInTransaction(context -> {
            //cerco la spiaggia dell'Owner
            Beach beach = beachRepository.findByOwnerId(ownerId, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Owner has no beach"));

            //cerco tutti i Booking di quella spiaggia
            return bookingRepository.findByBeachId(beach.getId(), context);
        });
    }

    /**
     * Metodo privato che serve nelle operazioni sensibili (in questo caso in update):<br>
     * Cerca in DB -> se non trovo la spiaggia, restituisce NULL -> interrompo tutto.
     *
     * @param id      Identificativo booking da cercare
     * @param context Connessione JDBC
     * @return oggetto Booking con quell'ID
     * @throws IllegalArgumentException se il booking non è stato trovato nel DB
     */
    private Booking getBookingOrThrow(Integer id, TransactionContext context) {
        return bookingRepository.findById(id, context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Booking not found with id: " + id));
    }

    /**
     * Metodo privato che va a fare questo calcolo:
     * numero parcheggi totali - numero parcheggi prenotati >= numero parcheggi richiesti.<br>
     * Questa operazione viene fatta per ciascuna categoria di parcheggio.
     *
     * @param capacity  Parcheggio della spiaggia
     * @param booked    Parcheggi prenotati in quella data
     * @param requested Parcheggi richiesti
     * @return una booleana che risponde alla domanda "ci sono parcheggi disponibili per questa prenotazione?"
     */
    private boolean isParkingAvailable(Parking capacity, BookedParkingSpaces booked, BookingParking requested) {
        if (capacity == null) return false;
        return (capacity.nAutoPark() - booked.bookedAuto() >= requested.autoPark()) &&
                (capacity.nMotoPark() - booked.bookedMoto() >= requested.motoPark()) &&
                (capacity.nBikePark() - booked.bookedBike() >= requested.bikePark()) &&
                (capacity.nElectricPark() - booked.bookedElectric() >= requested.electricPark());
    }
}