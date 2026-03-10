package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.common.TransactionContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//TODO: aggiungere implementazione parcheggi
//TODO: aggiungere possibilità di far prenotazioni da parte della balneazione per persone che telefonano allo stabilimento
/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database su oggetti di tipo Booking.
 * Implementata in:
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.JdbcBookingRepository JdbcBookingRepository
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager per le transazioni SQL
 */
public interface BookingRepository {
    Integer save(Booking booking, TransactionContext context);
    void delete(Integer id, TransactionContext context);
    void update(Booking booking, TransactionContext context);
    Optional<Booking> findById(Integer id, TransactionContext context);

    //FIXME: probabile metodo da mettere in un successivo Use Case
    //trova spot occupati per una data specifica
    List<Integer> findOccupiedSpots(Integer beachId, LocalDate date, TransactionContext context);
}