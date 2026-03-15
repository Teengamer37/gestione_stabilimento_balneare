package com.example.s_balneare.application.port.out.booking;

import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.booking.JdbcBookingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database su oggetti di tipo Booking.
 * Implementata in:
 * @see JdbcBookingRepository JdbcBookingRepository
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager per le transazioni SQL
 */
public interface BookingRepository {
    Integer save(Booking booking, TransactionContext context);
    void delete(Integer id, TransactionContext context);
    void update(Booking booking, TransactionContext context);
    Optional<Booking> findById(Integer id, TransactionContext context);

    //trova spot occupati per una data specifica
    List<Integer> findOccupiedSpots(Integer beachId, LocalDate date, Integer excludeBookingId, TransactionContext context);

    //trova tutte le prenotazioni fatte dal customer
    List<Booking> findByCustomerId(Integer customerId, TransactionContext context);

    //trova tutte le prenotazioni fatte in una spiaggia
    List<Booking> findByBeachId(Integer beachId, TransactionContext context);

    //controlla se l'utente ha booking passati in stato CONFIRMED in una determinata spiaggia
    boolean hasPastConfirmedBooking(Integer customerId, Integer beachId, LocalDate referenceDate, TransactionContext context);

    //annulla tutte le prenotazioni future di un customer (usato in caso di chiusura/ban account)
    void cancelFutureBookingsForCustomer(Integer customerId, LocalDate referenceDate, TransactionContext context);

    //annulla tutte le prenotazioni future di una spiaggia (usato in caso di chiusura/ban)
    void cancelFutureBookingsForBeach(Integer beachId, LocalDate referenceDate, TransactionContext context);

    //cerca il numero massimo di prenotazioni di posti auto per giorno (usato nel caso se in un aggiornamento dei parcheggi
    //vado a ridurre il numero di posti, lasciando quindi mezzi "vacanti")
    BookedParkingSpaces getMaxFutureParkings(Integer beachId, LocalDate referenceDate, TransactionContext context);

    //cerca il numero massimo di prenotazioni di oggetti extra per giorno (usato nel caso se in un aggiornamento dell'inventario
    //vado a ridurre il numero di oggetti, lasciando quindi prenotazioni senza gli oggetti prenotati)
    BookedInventory getMaxFutureInventory(Integer beachId, LocalDate referenceDate, TransactionContext context);
}