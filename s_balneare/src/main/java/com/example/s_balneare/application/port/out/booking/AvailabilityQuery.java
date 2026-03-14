package com.example.s_balneare.application.port.out.booking;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.booking.JdbcAvailabilityQuery;

import java.time.LocalDate;

/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database per controllare la
 * disponibilità dei parcheggi di una spiaggia.
 * Implementata in:
 * @see JdbcAvailabilityQuery JdbcAvailabilityQuery
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public interface AvailabilityQuery {
    BookedParkingSpaces getBookedParking(Integer beachId, LocalDate date, Integer excludeBookingId, TransactionContext context);
    BookedInventory getBookedInventory(Integer beachId, LocalDate date, Integer excludeBookingId, TransactionContext context);
}