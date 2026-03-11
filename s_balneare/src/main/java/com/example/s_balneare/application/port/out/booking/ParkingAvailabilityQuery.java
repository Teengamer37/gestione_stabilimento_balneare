package com.example.s_balneare.application.port.out.booking;

import com.example.s_balneare.domain.common.TransactionContext;

import java.time.LocalDate;

/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database per controllare la
 * disponibilità dei parcheggi di una spiaggia.
 * Implementata in:
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.booking.JdbcParkingAvailabilityQuery JdbcParkingAvailabilityQuery
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager per le transazioni SQL
 */
public interface ParkingAvailabilityQuery {
    BookedParkingSpaces getBookedSpacesForDate(Integer beachId, LocalDate date, TransactionContext context);
}