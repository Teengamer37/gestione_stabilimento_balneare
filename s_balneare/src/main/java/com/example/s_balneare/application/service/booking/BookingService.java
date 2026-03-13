package com.example.s_balneare.application.service.booking;

import com.example.s_balneare.application.port.in.booking.BookingUseCase;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;
import com.example.s_balneare.domain.common.TransactionContext;

import java.util.List;

//TODO: aggiungere possibilità di far prenotazioni da parte della balneazione per persone che telefonano allo stabilimento
/**
 * Implementazione dell'interfaccia che permette la manipolazione della collezione di Booking tra l'app Java e il Database.
 * @see BookingUseCase BookingUseCase
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager per le transazioni SQL
 */
public class BookingService implements BookingUseCase {
    private final BookingRepository bookingRepository;
    private final BeachRepository beachRepository;
    private final TransactionManager transactionManager;

    public BookingService(BookingRepository bookingRepository, TransactionManager transactionManager, BeachRepository beachRepository) {
        this.bookingRepository = bookingRepository;
        this.transactionManager = transactionManager;
        this.beachRepository = beachRepository;
    }

    //TODO: modificare tutta la logica update per UC-06
    /**
     * Modifica booking nel DB
     * @param booking Nuovo booking da aggiungere
     * @param availableParking Parcheggi disponibili per quella data in quella spiaggia
     * @return ID del booking aggiunto generato dal Database
     */
    @Override
    public Integer updateBooking(Booking booking, BookingParking availableParking) {
        return transactionManager.executeInTransaction(context -> {
            //check spot occupati
            List<Integer> occupiedSpots = bookingRepository.findOccupiedSpots(booking.getBeachId(), booking.getDate(), context);

            for (Integer spotId : booking.getSpotIds()) {
                if (occupiedSpots.contains(spotId)) {
                    throw new IllegalArgumentException("ERROR: Spot " + spotId + " is already occupied on " + booking.getDate());
                }
            }

            //check disponibilità parcheggio
            BookingParking requestedParking = booking.getParking();

            if (requestedParking != null) {
                if (requestedParking.autoPark() > availableParking.autoPark() ||
                        requestedParking.motoPark() > availableParking.motoPark() ||
                        requestedParking.bikePark() > availableParking.bikePark() ||
                        requestedParking.electricPark() > availableParking.electricPark()) {
                    throw new IllegalStateException("ERROR: not enough parking spaces available for this date");
                }
            }

            //check se gli spot appartengono effettivamente alla spiaggia
            if (!beachRepository.doSpotsBelongToBeach(booking.getBeachId(), booking.getSpotIds(), context)) {
                throw new SecurityException("ERROR: one or more spots do not belong to the beach");
            }

            //salva booking nel DB
            return bookingRepository.save(booking, context);
        });
    }

    /**
     * Ricerca, aggiorna stato in CONFIRMED e salva booking nel DB
     * @param id Identificatore booking da manipolare nel DB
     */
    @Override
    public void confirmBooking(Integer id) {
        transactionManager.executeInTransaction(context -> {
            Booking booking = getBookingOrThrow(id, context);

            booking.confirmBooking();

            bookingRepository.update(booking, context);
        });
    }

    /**
     * Ricerca, aggiorna stato in REJECTED e salva booking nel DB
     * @param id Identificatore booking da manipolare nel DB
     */
    @Override
    public void rejectBooking(Integer id) {
        transactionManager.executeInTransaction(context -> {
            Booking booking = getBookingOrThrow(id, context);

            booking.rejectBooking();

            bookingRepository.update(booking, context);
        });
    }

    /**
     * Ricerca, aggiorna stato in CANCELLED e salva booking nel DB
     * @param id Identificatore booking da manipolare nel DB
     */
    @Override
    public void cancelBooking(Integer id) {
        transactionManager.executeInTransaction(context -> {
            Booking booking = getBookingOrThrow(id, context);

            booking.cancelBooking();

            bookingRepository.update(booking, context);
        });
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
    @Override
    public void addExtras (Integer id,
                           int extraSdraio, int extraLettini,
                           int extraCamerini, int extraSedie,
                           int availableSdraio, int availableLettini,
                           int availableCamerini, int availableSedie) {
        transactionManager.executeInTransaction(context -> {
            Booking booking = getBookingOrThrow(id, context);

            if (extraSdraio > 0) booking.addExtraSdraio(extraSdraio, availableSdraio);
            if (extraLettini > 0) booking.addExtraLettini(extraLettini, availableLettini);
            if (extraCamerini > 0) booking.addCamerini(extraCamerini, availableCamerini);
            if (extraSedie > 0) booking.addExtraSedie(extraSedie, availableSedie);

            bookingRepository.update(booking, context);
        });
    }

    /**
     * Ricerca, aggiorna i parcheggi extra e salva booking nel DB
     * @param id Identificatore booking da manipolare nel DB
     * @param extraParking Oggetto contenente i parcheggi extra richiesti
     * @param availableParking Oggetto contenente i parcheggi attualmente disponibili
     */
    @Override
    public void addExtraParking(Integer id, BookingParking extraParking, BookingParking availableParking) {
        transactionManager.executeInTransaction(context -> {
            Booking booking = getBookingOrThrow(id, context);

            //delega la logica di validazione all'entità Booking (che controllerà requested <= available)
            booking.addExtraParking(extraParking, availableParking);

            bookingRepository.update(booking, context);
        });
    }

    /**
     * Metodo privato che serve nelle operazioni sensibili (in questo caso in update):
     * cerca in DB -> se non trovo la spiaggia, restituisce NULL -> interrompo tutto
     * @param id Identificativo booking da cercare
     * @param context Connessione JDBC
     * @return oggetto Booking con quell'ID
     * @throws IllegalArgumentException se il booking non è stato trovato nel DB
     */
    private Booking getBookingOrThrow(Integer id, TransactionContext context) {
        return bookingRepository.findById(id, context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Booking not found with id: " + id));
    }
}